import {
  createParamDecorator,
  ExecutionContext,
  UnauthorizedException,
} from '@nestjs/common';
import { Request } from 'express';

type RequestComUserId = Request & { userId?: string };

export const activeUserId = createParamDecorator<undefined>(
  (data, context: ExecutionContext) => {
    const request = context.switchToHttp().getRequest<RequestComUserId>();

    if (!request.userId) {
      throw new UnauthorizedException('Usuário não autenticado');
    }

    return request.userId;
  },
);
