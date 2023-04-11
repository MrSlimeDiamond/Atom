const IRCCommand = require("../irccommand");

class HelpCommand extends IRCCommand {
  constructor(irc) {
    super(irc);

    this.name = "help";
    this.aliases = ["h"];
    this.description = "Show the bot's commands";
  }

  onCommand(client, from, to, message) {
    let cmds = Array.from(this.getCommandHandler().commands.values());
    let commands = [];
    let args = this.getArgs(message, from);

    for (const command of cmds) {
      if (!command.adminonly) commands.push(command.name);
    }

    if (args.length == 0) {
      let msg = "My commands are: " + commands.join(", ");

      this.sendIRCMessage(to, msg, this.isHidden(message, from));
      this.sendIRCMessage(
        to,
        "Prefix: " + this.prefix,
        this.isHidden(message, from)
      );
    } else if (args.length == 1) {
      // help for a specific command

      let command_query = args[0];
      console.log(cmds);
      if (!commands.includes(command_query)) {
        this.sendIRCMessage(
          to,
          "I don't recognize that command",
          this.isHidden(message, from)
        );
        return;
      }

      let commandID = cmds.indexOf(command_query);
      let command = cmds[commandID];
      console.log(command_query);
      console.log(commandID);
      console.log(command);
      this.sendIRCMessage(
        to,
        command.name + " : " + command.description,
        this.isHidden(message, from)
      );
    } else {
      this.sendIRCMessage(
        to,
        "No clue what you're tying to do",
        this.isHidden(message, from)
      );
    }
  }
}

module.exports = HelpCommand;
