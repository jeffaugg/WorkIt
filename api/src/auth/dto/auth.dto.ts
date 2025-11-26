import { IsNotEmpty, IsString, MinLength, IsEmail } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class AuthDto {
  @ApiProperty({
    example: 'usuario@exemplo.com',
    description: 'E-mail do usuário',
  })
  @IsString()
  @IsNotEmpty({
    message: 'email não pode ser vazio',
  })
  @IsEmail({}, {
    message: 'email deve ser um endereço de e-mail válido',
  })
  email: string;

  @ApiProperty({
    example: 'minhasenha123',
    description: 'Senha do usuário com pelo menos 8 caracteres',
    minLength: 8,
  })
  @IsString()
  @IsNotEmpty({
    message: 'senha não pode ser vazia',
  })
  @MinLength(8, {
    message: 'senha deve ter pelo menos 8 caracteres',
  })
  password: string;
}
