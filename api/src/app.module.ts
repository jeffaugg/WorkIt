import { Module } from '@nestjs/common';
import { PrismaModule } from './prisma/prisma.module';
import { UsersModule } from './users/users.module';
import { AuthModule } from './auth/auth.module';
import { GroupsModule } from './groups/groups.module';
import { AuthGuard } from './auth/auth.guard';
import { PostsModule } from './posts/groups.module';
import { StorageModule } from './storage/storage.module';

@Module({
  imports: [
    PrismaModule,
    UsersModule,
    AuthModule,
    GroupsModule,
    PostsModule,
    StorageModule,
  ],
  controllers: [],
  providers: [
    {
      provide: 'APP_GUARD',
      useClass: AuthGuard,
    },
  ],
})
export class AppModule {}
