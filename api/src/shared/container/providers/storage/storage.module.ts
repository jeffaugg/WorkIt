import { Module, Global } from '@nestjs/common';
import { StorageProvider } from './interface/storage.provider';
import { S3StorageProvider } from './s3-storage.provider';

@Global()
@Module({
  providers: [
    {
      provide: StorageProvider,
      useClass: S3StorageProvider,
    },
  ],
  exports: [StorageProvider],
})
export class StorageProviderModule {}
