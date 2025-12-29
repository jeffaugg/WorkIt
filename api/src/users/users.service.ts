import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateUserDto } from './dto/update-user.dto';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService) {}

  async findAll() {
    const users = await this.prisma.user.findMany({
      include: {
        groups: {
          include: {
            group: true,
          },
        },
      },
    });

    return users.map((user) => ({
      id: user.id,
      email: user.email,
      name: user.name,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
      groups: user.groups.map((g) => ({
        id: g.group.id,
        name: g.group.name,
      })),
    }));
  }

  async findOne(id: string) {
    const user = await this.prisma.user.findUnique({
      where: { id },
      include: {
        groups: {
          include: {
            group: true,
          },
        },
      },
    });

    if (!user) {
      return null; 
    }

    return {
      id: user.id,
      email: user.email,
      name: user.name,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
      groups: user.groups.map((g) => ({
        id: g.group.id,
        name: g.group.name,
      })),
    };
  }

  update(id: string, updateUserDto: UpdateUserDto) {
    return this.prisma.user.update({ where: { id }, data: updateUserDto });
  }

  remove(id: string) {
    return this.prisma.user.delete({ where: { id } });
  }
}
