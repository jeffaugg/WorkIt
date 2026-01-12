export abstract class StorageProvider {
  abstract uploadFile(
    file: Buffer,
    filename: string,
    mimeType: string,
  ): Promise<string>;
  abstract deleteFile(path: string): Promise<void>;
}
