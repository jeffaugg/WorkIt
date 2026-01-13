import { Injectable, OnModuleInit } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  S3Client,
  PutObjectCommand,
  HeadBucketCommand,
  DeleteObjectCommand,
  PutBucketPolicyCommand,
  CreateBucketCommand,
  GetObjectCommand,
} from '@aws-sdk/client-s3';
import { randomUUID } from 'crypto';
import { StorageProvider } from './interface/storage.provider';
import { Readable } from 'stream';

@Injectable()
export class S3StorageProvider implements StorageProvider, OnModuleInit {
  private client: S3Client;
  private bucket: string;

  constructor(private readonly configService: ConfigService) {
    this.bucket = this.configService.getOrThrow<string>('AWS_BUCKET_NAME');

    this.client = new S3Client({
      endpoint: this.configService.get<string>('AWS_ENDPOINT'),
      region: this.configService.get<string>('AWS_REGION', 'us-east-1'),
      credentials: {
        accessKeyId: this.configService.getOrThrow<string>('AWS_ACCESS_KEY_ID'),
        secretAccessKey: this.configService.getOrThrow<string>(
          'AWS_SECRET_ACCESS_KEY',
        ),
      },
      forcePathStyle: true,
    });
  }

  async onModuleInit() {
    try {
      await this.client.send(new HeadBucketCommand({ Bucket: this.bucket }));
    } catch {
      console.log(`Bucket ${this.bucket} não encontrado. Criando...`);
      await this.client.send(new CreateBucketCommand({ Bucket: this.bucket }));
      const policy = {
        Version: '2012-10-17',
        Statement: [
          {
            Sid: 'PublicReadGetObject',
            Effect: 'Allow',
            Principal: '*',
            Action: ['s3:GetObject'],
            Resource: [`arn:aws:s3:::${this.bucket}/*`],
          },
        ],
      };

      await this.client.send(
        new PutBucketPolicyCommand({
          Bucket: this.bucket,
          Policy: JSON.stringify(policy),
        }),
      );
    }
  }

  async uploadFile(
    file: Buffer,
    filename: string,
    mimeType: string,
  ): Promise<string> {
    const extension = filename.split('.').pop();
    const uniqueName = extension
      ? `${randomUUID()}.${extension}`
      : randomUUID();

    await this.client.send(
      new PutObjectCommand({
        Bucket: this.bucket,
        Key: uniqueName,
        Body: file,
        ContentType: mimeType,
        ACL: 'public-read',
      }),
    );

    const endpoint =
      this.configService.get<string>('AWS_PUBLIC_URL') ||
      this.configService.get<string>('AWS_ENDPOINT');
    return `${endpoint}/storage/files/${uniqueName}`;
  }

  async deleteFile(path: string): Promise<void> {
    const key = path.split('/').pop();

    await this.client.send(
      new DeleteObjectCommand({
        Bucket: this.bucket,
        Key: key,
      }),
    );
  }

  async getFile(key: string): Promise<{ buffer: Buffer; contentType: string }> {
    const res = await this.client.send(
      new GetObjectCommand({ Bucket: this.bucket, Key: key }),
    );

    const body = res.Body;

    if (!body) {
      throw new Error('Empty body');
    }
    const stream = body as Readable;

    const buffer = await streamToBuffer(stream);

    return {
      buffer,
      contentType: res.ContentType || 'application/octet-stream',
    };
  }
}

function streamToBuffer(stream: Readable): Promise<Buffer> {
  return new Promise((resolve, reject) => {
    const chunks: Buffer[] = [];

    stream.on('data', (chunk: unknown) => {
      if (Buffer.isBuffer(chunk)) chunks.push(chunk);
      else chunks.push(Buffer.from(chunk as Uint8Array));
    });

    stream.on('end', () => resolve(Buffer.concat(chunks)));
    stream.on('error', reject);
  });
}
