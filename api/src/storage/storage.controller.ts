import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  ParseFilePipe,
  MaxFileSizeValidator,
  FileTypeValidator,
  HttpCode,
  HttpStatus,
  Get,
  Param,
  NotFoundException,
  StreamableFile,
  Res,
} from '@nestjs/common';
import type { Response } from 'express';
import { FileInterceptor } from '@nestjs/platform-express';
import { StorageProvider } from 'src/shared/container/providers/storage/interface/storage.provider';

@Controller('storage')
export class StorageController {
  constructor(private readonly storageProvider: StorageProvider) {}

  @Post('upload')
  @UseInterceptors(FileInterceptor('file'))
  @HttpCode(HttpStatus.CREATED)
  async uploadFile(
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: 5 * 1024 * 1024 }),
          new FileTypeValidator({ fileType: '.(png|jpeg|jpg|webp)' }),
        ],
      }),
    )
    file: Express.Multer.File,
  ) {
    const url = await this.storageProvider.uploadFile(
      file.buffer,
      file.originalname,
      file.mimetype,
    );

    return {
      message: 'Upload realizado com sucesso',
      url: url,
    };
  }

  @Get('files/:key')
  async getFile(
    @Param('key') key: string,
    @Res({ passthrough: true }) res: Response,
  ) {
    try {
      const file = await this.storageProvider.getFile(key);

      res.set('Content-Type', file.contentType);
      res.set('Cache-Control', 'public, max-age=31536000, immutable');

      return new StreamableFile(file.buffer);
    } catch {
      throw new NotFoundException('Arquivo não encontrado');
    }
  }
}
