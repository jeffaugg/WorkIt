import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateGroupDto } from './dto/create-group.dto';
import { UpdateGroupDto } from './dto/update-group.dto';

@Injectable()
export class GroupsService {
  constructor(private prisma: PrismaService) {}

  create(createGroupDto: CreateGroupDto) {
    return this.prisma.group.create({ data: createGroupDto });
  }

  async findAll() {
    const groups = await this.prisma.group.findMany({
      include: {
        users: {
          include: {
            user: true,
          },
        },
      },
    });

    return groups.map((group) => ({
      id: group.id,
      name: group.name,
      imageUrl: group.imgUrl ?? null,
      description: group.description ?? null,
      createdAt: group.createdAt,
      updatedAt: group.updatedAt ?? null,
      users: group.users.map((gu) => ({
        id: gu.user.id,
        name: gu.user.name,
      })),
    }));
  }

  async findOne(id: string) {
    const group = await this.prisma.group.findUnique({
      where: { id },
      include: {
        users: {
          include: {
            user: true,
          },
        },
      },
    });

    if (!group) {
      return null; 
    }

    return {
      id: group.id,
      name: group.name,
      imageUrl: group.imgUrl ?? null,
      description: group.description ?? null,
      createdAt: group.createdAt,
      updatedAt: group.updatedAt ?? null,
      users: group.users.map((gu) => ({
        id: gu.user.id,
        name: gu.user.name,
      })),
    };
  }

  update(id: string, updateGroupDto: UpdateGroupDto) {
    return this.prisma.group.update({ where: { id }, data: updateGroupDto });
  }

  remove(id: string) {
    return this.prisma.group.delete({ where: { id } });
  }
  
  addUserToGroup(groupId: string, userId: string) {
    return this.prisma.groupUser.create({
      data: {
        groupId,
        userId,
      },
    });
  }

  removeUserFromGroup(groupId: string, userId: string) {
    return this.prisma.groupUser.delete({
      where: {
        userId_groupId: {
          userId,
          groupId,
        },
      },
    });
  }
}
