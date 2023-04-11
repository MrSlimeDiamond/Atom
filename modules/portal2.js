const { EmbedBuilder } = require('@discordjs/builders')
const modules = require('../modules')
const config = require('../config.json')
const fs = require('node:fs')
const axios = require('axios')
const exec = require('child_process').exec
const express = require('express')
const logger = require('../logger')
const log = new logger('Portal 2 Module')
const bot = require('../index')
const path = require('node:path')

let queue = []
let rendering = false

async function addQueue(url, message) {
    queue.push([url, message])
}

async function renderNext() {
    if (queue.length == 0 || rendering) {
        // the queue is empty, don't do anything
        return
    } else {
        rendering = true
        const url = queue[0][0]
        const message = queue[0][1]
        queue.shift()

        axios({
            method: 'get',
            url: url,
            responseType: 'stream',
        }).then(function (response) {
            response.data.pipe(fs.createWriteStream('./portal2/demo.dem'))
        })

        //log.info(config.modules.portal2.portal2Path)

        let portal2LaunchScript =
            path.join(config.modules.portal2.portal2Path, 'portal2.exe') +
            ' -novid'
        log.info(portal2LaunchScript)

        exec(
            portal2LaunchScript,
            function (error, stdout, stderr) {} // idk what this does tbh
        ).on('exit', () => {
            // Assume the render finished
            let renderNumber = Math.floor(Math.random() * 999999999999) // pray that the ID doesn't already exist, it probably will work most of the time
            log.info('Starting render! ID: ' + renderNumber)
            if (!fs.existsSync('./portal2/renders/' + message.author.id)) {
                fs.mkdirSync('./portal2/renders/' + message.author.id)
            }
            fs.rename(
                config.modules.portal2.portal2Path + '/portal2/demo.dem.mp4',
                '.\\portal2\\renders\\' +
                    message.author.id +
                    '\\' +
                    renderNumber +
                    '.mp4',
                err => {
                    if (err) {
                        const embed = new EmbedBuilder()
                            .setColor(0xf44141)
                            .setAuthor({
                                name: message.author.username,
                                iconURL: message.author.avatarURL(),
                            })
                            .setTitle('Render failed!')
                            .setDescription(
                                'The render completed, but the demo could not be renamed. If this persists, tell SlimeDiamond.'
                            )
                        message.reply({ embeds: [embed] })
                        log.error(err)
                        return
                    }

                    let videoURL =
                        'http://' +
                        config.modules.portal2.baseURL +
                        '?user=' +
                        message.author.id +
                        '&id=' +
                        renderNumber

                    /*
            const embed = new EmbedBuilder()
            .setColor(0x4feb34)
            .setAuthor({ name: message.author.username, iconURL: message.author.avatarURL() })
            .setTitle("Render finished!")
            //.addFields(
              //{ name: "URL", value: videoURL }
            //)
            .addFields(
              { name: "ID", value: renderNumber }
            )

            message.channel.send({content: "<@" + message.author.id + "> " + videoURL, embeds: [embed]})
            */

                    message.channel.send(
                        '<@' +
                            message.author.id +
                            '> Your render is finished! URL: ' +
                            videoURL
                    )

                    rendering = false
                }
            )
        })
    }
}

async function portal2Module(client) {
    client.on('ready', ready => {
        ;(function () {
            renderNext()
            setTimeout(arguments.callee, 60)
        })()
    })

    const app = express()
    //app.use("/renders", express.static("../portal2/renders"))

    app.get('/render', (req, res) => {
        let user = req.query.user
        let id = req.query.id
        if (!user || !id) {
            res.send(400)
            return
        }
        let videoFile =
            bot.__dirname + '/portal2/renders/' + user + '/' + id + '.mp4'
        res.sendFile(videoFile)
    })

    app.listen(8080, () => {
        log.info('Listening on 8080')
    })

    client.on('messageCreate', async message => {
        if (!modules[2].enabledGuilds.includes(message.guildId)) return
        if (message.content.startsWith('!render-demo')) {
            if (message.attachments.length < 0) {
                const embed = new EmbedBuilder()
                    .setColor(0xf44141)
                    .setAuthor({
                        name: message.author.username,
                        iconURL: message.author.avatarURL(),
                    })
                    .setTitle('Render failed!')
                    .setDescription(
                        'Please upload **1** Portal 2 demo file with the command.'
                    )
                message.reply({ embeds: [embed] })
                return
            }
            addQueue(message.attachments.first().url, message)

            const embed = new EmbedBuilder()
                .setColor(0x4feb34)
                .setAuthor({
                    name: message.author.username,
                    iconURL: message.author.avatarURL(),
                })
                .setTitle('Render queued!')
                .setDescription(
                    'Render was successfully queued at position ' +
                        queue.length +
                        ' . This can take a while. You will be notified when it is finished.'
                )

            message.reply({ embeds: [embed] })
        }
    })
}

module.exports.module = portal2Module
