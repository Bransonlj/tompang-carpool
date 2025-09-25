import { Injectable, Logger, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';

@Injectable()
export class LoggingMiddleware implements NestMiddleware {
  private readonly logger = new Logger(LoggingMiddleware.name);

  use(req: Request, res: Response, next: NextFunction) {
    const { method, originalUrl } = req;
    const start = Date.now();

    // Log incoming request
    this.logger.log(
      `Request: ${method} ${originalUrl} | Params: ${JSON.stringify(
        req.params,
      )} | Query: ${JSON.stringify(req.query)} | Body: ${JSON.stringify(
        req.body,
      )}`,
    );

    next();
  }
}