import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Put,
  Req,
} from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { CreateGroupDto } from './dto/create-group.dto';
import { UpdateGroupDto } from './dto/update-group.dto';
import { GroupsService } from './groups.service';

type AuthRequest = Request & { user: { id: string } };

@ApiBearerAuth('JWT-auth')
@ApiTags('groups')
@Controller('groups')
export class GroupsController {
  constructor(private readonly groupsService: GroupsService) {}

  @Post()
  create(@Body() createGroupDto: CreateGroupDto, @Req() req: AuthRequest) {
    return this.groupsService.create(createGroupDto, req.user.id);
  }

  @Get()
  findAll() {
    return this.groupsService.findAll();
  }

  @Get('explore/:userId')
  findGroupsNotJoined(@Param('userId') userId: string) {
    return this.groupsService.findGroupsNotJoined(userId);
  }

  @Get('search/:name')
  searchByName(@Param('name') name: string) {
    return this.groupsService.searchByName(name);
  }

  @Get('user/:userId/search/:name')
  searchUserGroups(
    @Param('userId') userId: string,
    @Param('name') name: string,
  ) {
    return this.groupsService.searchUserGroups(userId, name);
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
    @Param('id') groupId: string,
    @Param('userId') userId: string,
  ) {
    return this.groupsService.addUserToGroup(groupId, userId);
  }

  @Delete(':id/users/:userId')
  removeUserFromGroup(
    @Param('id') groupId: string,
    @Param('userId') userId: string,
  ) {
    return this.groupsService.removeUserFromGroup(groupId, userId);
  }
}
