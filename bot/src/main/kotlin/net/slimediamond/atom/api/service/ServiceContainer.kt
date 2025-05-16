package net.slimediamond.atom.api.service

import org.apache.logging.log4j.Logger

data class ServiceContainer(val name: String, val instance: Any, val logger: Logger)