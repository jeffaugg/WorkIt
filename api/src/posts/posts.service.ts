import { BadRequestException, Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreatePostDto } from './dto/create-post.dto';
import { UpdatePostDto } from './dto/update-post.dto';

@Injectable()
export class PostsService {
  constructor(private prisma: PrismaService) {}

  async create(createPostDto: CreatePostDto) {
    const { title, activityType, body, imageUrl, location, groupId, userId } =
      createPostDto;

    if (!userId) {
      throw new BadRequestException('userId is required');
    }

    const post = await this.prisma.post.create({
      data: {
        title,
        activityType,
        groupId,
        userId,
        ...(body !== undefined && { body }),
        ...(imageUrl !== undefined && { imageUrl }),
        ...(location !== undefined && { location }),
      },
      include: {
        user: true,
        group: {
          include: {
            users: {
              include: {
                user: true,
              },
            },
          },
        },
      },
    });

    return {
      id: post.id,
      title: post.title,
      activityType: post.activityType,
      body: post.body ?? null,
      imageUrl: post.imageUrl ?? null,
      location: post.location ?? null,
      createdAt: post.createdAt,
      updatedAt: post.updatedAt ?? null,
      user: {
        id: post.user.id,
        name: post.user.name,
      },
      group: {
        id: post.group.id,
        name: post.group.name,
        imageUrl: post.group.imgUrl ?? null,
        description: post.group.description ?? null,
        createdAt: post.group.createdAt,
        updatedAt: post.group.updatedAt ?? null,
        users: post.group.users.map((gu) => ({
          id: gu.user.id,
          name: gu.user.name,
        })),
      },
    };
  }

  async findAll() {
    const posts = await this.prisma.post.findMany({
      include: {
        user: true,
        group: {
          include: {
            users: {
              include: {
                user: true,
              },
            },
          },
        },
      },
      orderBy: {
        createdAt: 'desc',
      },
    });

    return posts.map((post) => ({
      id: post.id,
      title: post.title,
      activityType: post.activityType,
      body: post.body ?? null,
      imageUrl: post.imageUrl ?? null,
      location: post.location ?? null,
      createdAt: post.createdAt,
      updatedAt: post.updatedAt ?? null,
      user: {
        id: post.user.id,
        name: post.user.name,
      },
      group: {
        id: post.group.id,
        name: post.group.name,
        imageUrl: post.group.imgUrl ?? null,
        description: post.group.description ?? null,
        createdAt: post.group.createdAt,
        updatedAt: post.group.updatedAt ?? null,
        users: post.group.users.map((gu) => ({
          id: gu.user.id,
          name: gu.user.name,
        })),
      },
    }));
  }

  async findOne(id: string) {
    const post = await this.prisma.post.findUnique({
      where: { id },
      include: {
        user: true,
        group: {
          include: {
            users: {
              include: {
                user: true,
              },
            },
          },
        },
      },
    });

    if (!post) {
      return null;
    }

    return {
      id: post.id,
      title: post.title,
      activityType: post.activityType,
      body: post.body ?? null,
      imageUrl: post.imageUrl ?? null,
      location: post.location ?? null,
      createdAt: post.createdAt,
      updatedAt: post.updatedAt ?? null,
      user: {
        id: post.user.id,
        name: post.user.name,
      },
      group: {
        id: post.group.id,
        name: post.group.name,
        imageUrl: post.group.imgUrl ?? null,
        description: post.group.description ?? null,
        createdAt: post.group.createdAt,
        updatedAt: post.group.updatedAt ?? null,
        users: post.group.users.map((gu) => ({
          id: gu.user.id,
          name: gu.user.name,
        })),
      },
    };
  }

  async update(id: string, updatePostDto: UpdatePostDto) {
    const { title, activityType, body, imageUrl, location, groupId, userId } =
      updatePostDto;

    return this.prisma.post.update({
      where: { id },
      data: {
        ...(title !== undefined && { title }),
        ...(activityType !== undefined && { activityType }),
        ...(body !== undefined && { body }),
        ...(imageUrl !== undefined && { imageUrl }),
        ...(location !== undefined && { location }),
        ...(groupId !== undefined && { groupId }),
        ...(userId !== undefined && { userId }),
      },
    });
  }

  remove(id: string) {
    return this.prisma.post.delete({ where: { id } });
  }

  async findByGroup(groupId: string) {
    const posts = await this.prisma.post.findMany({
      where: { groupId },
      include: {
        user: true,
        group: {
          include: {
            users: {
              include: {
                user: true,
              },
            },
          },
        },
      },
      orderBy: {
        createdAt: 'desc',
      },
    });

    return posts.map((post) => ({
      id: post.id,
      title: post.title,
      activityType: post.activityType,
      body: post.body ?? null,
      imageUrl: post.imageUrl ?? null,
      location: post.location ?? null,
      createdAt: post.createdAt,
      updatedAt: post.updatedAt ?? null,
      user: {
        id: post.user.id,
        name: post.user.name,
      },
      group: {
        id: post.group.id,
        name: post.group.name,
        imageUrl: post.group.imgUrl ?? null,
        description: post.group.description ?? null,
        createdAt: post.group.createdAt,
        updatedAt: post.group.updatedAt ?? null,
        users: post.group.users.map((gu) => ({
          id: gu.user.id,
          name: gu.user.name,
        })),
      },
    }));
  }
}
