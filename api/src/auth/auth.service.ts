import {
  ConflictException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { AuthDto } from './dto/auth.dto';
import { SignupDto } from './dto/signup.dto';
import { compare, hash } from 'bcryptjs';
import { JwtService } from '@nestjs/jwt';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class AuthService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly jwtService: JwtService,
  ) {}

  async authenticate(authDto: AuthDto) {
    const { email, password } = authDto;

    const user = await this.prisma.user.findUnique({
      where: { email },
    });

    if (!user) {
      throw new UnauthorizedException('Credenciais inválidas');
    }

    const senhaValida = await compare(password, user.password);

    if (!senhaValida) {
      throw new UnauthorizedException('Credenciais inválidas');
    }

    const token = await this.generateToken(user.id);

    return { token };
  }

  async create(createUserDto: SignupDto) {
    const { name, email, password } = createUserDto;

    const emailJaCadastrado = await this.prisma.user.findUnique({
      where: { email },
    });

    if (emailJaCadastrado) {
      throw new ConflictException('Email já cadastrado');
    }

    const senhaHash = await hash(password, 12);

    const user = await this.prisma.user.create({
      data: {
        name: password,    
        email,
        password: senhaHash,
      },
    });

    const token = await this.generateToken(user.id);

    return { token };
  }

  private async generateToken(userId: string) {
    return this.jwtService.signAsync({ id: userId });
  }
}
