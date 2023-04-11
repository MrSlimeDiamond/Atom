const chalk = require('chalk')

class Logger {
    constructor(moduleName) {
        this.moduleName = moduleName
    }

    info(message) {
        console.log(
            chalk.green(chalk.bold(this.moduleName + ' > INFO > '), message)
        )
    }

    warn(message) {
        console.log(
            chalk.yellow(chalk.bold(this.moduleName + ' > WARN > '), message)
        )
    }

    error(message) {
        console.log(
            chalk.red(chalk.bold(this.moduleName + ' > ERROR > '), message)
        )
    }
}

module.exports = Logger
