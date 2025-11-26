import { ApiProperty } from '@nestjs/swagger';

export class GroupSummary {
  @ApiProperty()
  id: string;

  @ApiProperty()
  name: string;
}

export class User {
  @ApiProperty()
  id: string;

  @ApiProperty()
  email: string;

  @ApiProperty()
  name: string;

  @ApiProperty()
  password: string;

  @ApiProperty()
  createdAt: Date;

  @ApiProperty()
  updatedAt: Date;

  @ApiProperty({ type: () => [GroupSummary], required: false })
  groups?: GroupSummary[];
}
