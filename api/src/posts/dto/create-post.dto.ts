import { ApiProperty } from '@nestjs/swagger';
import {
  IsEnum,
  IsNotEmpty,
  IsOptional,
  IsString,
  IsUUID,
} from 'class-validator';
import { ActivityType } from '@prisma/client';

export class CreatePostDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  title: string;

  @ApiProperty({ enum: ActivityType })
  @IsEnum(ActivityType)
  activityType: ActivityType;

  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  body?: string;

  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  imageUrl?: string;

  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  location?: string;

  @ApiProperty()
  @IsUUID()
  @IsNotEmpty()
  groupId: string;

  @ApiProperty({
    required: false,
    description: 'If omitted, it will be taken from the authenticated user',
  })
  @IsUUID()
  @IsOptional()
  userId?: string;
}
