package net.slimediamond.atom.service

import org.apache.logging.log4j.Logger

data class ServiceContainer(val name: String, val instance: Any, val logger: Logger)