import {
  ArgumentMetadata,
  BadRequestException,
  Injectable,
  PipeTransform,
} from '@nestjs/common';
import { plainToInstance } from 'class-transformer';
import { validateSync } from 'class-validator';

@Injectable()
export class ParseAndValidateJsonPipe implements PipeTransform {
  constructor(private readonly targetType: any) {}

  transform(value: any, metadata: ArgumentMetadata) {
    let parsed;
    try {
      parsed = typeof value === 'string' ? JSON.parse(value) : value;
    } catch (err) {
      throw new BadRequestException('Invalid JSON format');
    }

    const object = plainToInstance(this.targetType, parsed);
    const errors = validateSync(object, { whitelist: true, forbidNonWhitelisted: true });

    if (errors.length > 0) {
      throw new BadRequestException(errors);
    }

    return object;
  }
}
