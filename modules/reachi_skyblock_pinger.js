// api.hypixel.net/status?uuid=5a9b8e02-7703-4be4-84d5-2c3ebfd14225&key=49732312-a9f1-4305-848c-9e32861a8f5f

const { EmbedBuilder } = require("@discordjs/builders");
const modules = require("../modules");
const config = require("../config.json");
const axios = require("axios");
let sendMsg = true;

async function reachiLogoff(client) {
  if (!modules[1].enabledGuilds.includes("826198598348701796")) return;
  if (sendMsg == false) return;
  const channel = client.channels.cache.get(
    config.guilds["826198598348701796"].notifyChannel
  );

  const embed = new EmbedBuilder()
    .setColor(0xffef00)
    .setAuthor({
      name: "Turborich logged off Skyblock!",
      iconURL: "https://mc-heads.net/avatar/Turborich",
    })
    .setDescription("Turborich is NOT on Skyblock!")
    .setTimestamp();

  channel.send({ content: "<@250455759635087360>", embeds: [embed] });
  sendMsg = false;
}

async function update(client) {
  axios
    .get(
      "https://api.hypixel.net/status?uuid=5a9b8e02-7703-4be4-84d5-2c3ebfd14225&key=49732312-a9f1-4305-848c-9e32861a8f5f"
    )
    .then((response) => {
      if (
        response["data"].session.online == false ||
        response["data"].session.gameType != "SKYBLOCK"
      ) {
        reachiLogoff(client);
      } else {
        // reachi has logged back online (or is already online when the bot started)
        sendMsg = true;
      }
    })

    .catch((error) => {
      console.error(error);
    });
}

async function reachiPingModule(client) {
  client.on("ready", (ready) => {
    (function () {
      update(client);
      setTimeout(arguments.callee, 60000);
    })();
  });
}

module.exports.module = reachiPingModule