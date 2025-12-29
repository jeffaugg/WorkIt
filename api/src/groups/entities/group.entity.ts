import { ApiProperty } from '@nestjs/swagger';

export class UserSummary {
  @ApiProperty()
  id: string;

  @ApiProperty()
  name: string;
}

export class Group {
  @ApiProperty()
  id: string;

  @ApiProperty()
  name: string;

  @ApiProperty()
  imageUrl?: string;

  @ApiProperty()
  description?: string;

  @ApiProperty()
  createdAt: Date;

  @ApiProperty()
  updatedAt?: Date;

  @ApiProperty({ type: () => [UserSummary], required: false })
  users?: UserSummary[];
}
