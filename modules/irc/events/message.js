const config = require("../../../module_configs/irc.json");
const logger = require("../../../logger");
const IRCCommandHandler = require("../handlers/command");
const log = new logger("IRC (Message Event)");

let handler;

async function handleEvent(client, from, to, message) {
  handler = new IRCCommandHandler(null, client);
  handler.handle(client, from, to, message);
}

module.exports.handleEvent = handleEvent;
module.exports.handler = IRCCommandHandler;
