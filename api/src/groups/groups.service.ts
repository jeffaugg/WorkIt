import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateGroupDto } from './dto/create-group.dto';
import { UpdateGroupDto } from './dto/update-group.dto';

@Injectable()
export class GroupsService {
  constructor(private prisma: PrismaService) {}

  create(createGroupDto: CreateGroupDto) {
    const { name, description, imageUrl } = createGroupDto;

    return this.prisma.group.create({
      data: {
        name,
        description,
        imgUrl: imageUrl,
      },
    });
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

  async findGroupsNotJoined(userId: string) {
    const groups = await this.prisma.group.findMany({
      where: {
        users: {
          none: {
            userId,
          },
        },
      },
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

  async searchByName(name: string) {
    const groups = await this.prisma.group.findMany({
      where: {
        name: {
          contains: name,
          mode: 'insensitive',
        },
      },
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

  async searchUserGroups(userId: string, name: string) {
    const groups = await this.prisma.group.findMany({
      where: {
        name: {
          contains: name,
          mode: 'insensitive',
        },
        users: {
          some: {
            userId,
          },
        },
      },
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
