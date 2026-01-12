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
} from '@nestjs/common';
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
}
