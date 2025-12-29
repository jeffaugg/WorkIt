import { Body, Controller, Inject, Post } from '@nestjs/common';
import { AuthDto } from './dto/auth.dto';
import { SignupDto } from './dto/signup.dto';
import { isPublic } from 'src/shared/decorators/isPublic';
import { AuthService } from './auth.service';

@isPublic()
@Controller('auth')
export class AuthController {
  constructor(
    private readonly authService: AuthService,
  ) {}

  @Post('login')
  authenticate(@Body() authDto: AuthDto) {
    return this.authService.authenticate(authDto);
  }

  @Post('register')
  create(@Body() signupDto: SignupDto) {
    return this.authService.create(signupDto);
  }
}
