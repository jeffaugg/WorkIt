export abstract class StorageProvider {
  abstract uploadFile(
    file: Buffer,
    filename: string,
    mimeType: string,
  ): Promise<string>;
  abstract deleteFile(path: string): Promise<void>;
  abstract getFile(
    key: string,
  ): Promise<{ buffer: Buffer; contentType: string }>;
}
