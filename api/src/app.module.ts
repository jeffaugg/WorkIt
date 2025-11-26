import { Module } from '@nestjs/common';
import { PrismaModule } from './prisma/prisma.module';
import { UsersModule } from './users/users.module';
import { AuthModule } from './auth/auth.module';
import { GroupsModule } from './groups/groups.module';
import { AuthGuard } from './auth/auth.guard';

@Module({
  imports: [PrismaModule, UsersModule, AuthModule, GroupsModule],
  controllers: [],
  providers: [
    {
      provide: 'APP_GUARD',
      useClass: AuthGuard,
    }
  ],
})
export class AppModule {}
