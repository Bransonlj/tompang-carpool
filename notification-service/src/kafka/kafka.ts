import { Logger } from "@nestjs/common";
import { Kafka, logCreator, LogEntry, logLevel } from "kafkajs";

const kafkaLogger: logCreator = (level: logLevel) => {
  const logger = new Logger('KafkaJS'); // context label

  return ({ namespace, level, label, log }: LogEntry) => {
    const { message, ...extra } = log;

    switch (level) {
      case logLevel.ERROR:
        logger.error(`[${namespace}] ${message}`, extra);
        break;
      case logLevel.WARN:
        logger.warn(`[${namespace}] ${message}`, extra);
        break;
      case logLevel.INFO:
        logger.log(`[${namespace}] ${message}`, extra);
        break;
      case logLevel.DEBUG:
        logger.debug?.(`[${namespace}] ${message} ${JSON.stringify(extra)}`);
        break;
      default:
        logger.verbose?.(`[${namespace}] ${message} ${JSON.stringify(extra)}`);
    }
  };
};

const kafka = new Kafka({
  clientId: 'tompang-carpool',
  brokers: ['localhost:9092'], // TODO move to config
  logCreator: kafkaLogger,
});

export default kafka;