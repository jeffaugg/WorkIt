import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  ParseIntPipe,
  Post,
  Put,
} from '@nestjs/common';
import { ApiTags } from '@nestjs/swagger';
import { CreateGroupDto } from './dto/create-group.dto';
import { UpdateGroupDto } from './dto/update-group.dto';
import { GroupsService } from './groups.service';

@ApiTags('groups')
@Controller('groups')
export class GroupsController {
  constructor(private readonly groupsService: GroupsService) {}

  @Post()
  create(@Body() createGroupDto: CreateGroupDto) {
    return this.groupsService.create(createGroupDto);
  }

  @Get()
  findAll() {
    return this.groupsService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.groupsService.findOne(id);
  }

  @Put(':id')
  update(@Param('id') id: string, @Body() updateGroupDto: UpdateGroupDto) {
    return this.groupsService.update(id, updateGroupDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.groupsService.remove(id);
  }

  @Post(':id/users/:userId')
  addUserToGroup(
    @Param('id', ParseIntPipe) groupId: string,
    @Param('userId') userId: string,
  ) {
    return this.groupsService.addUserToGroup(groupId, userId);
  }

  @Delete(':id/users/:userId')
  removeUserFromGroup(
    @Param('id', ParseIntPipe) groupId: string,
    @Param('userId') userId: string,
  ) {
    return this.groupsService.removeUserFromGroup(groupId, userId);
  }
}
