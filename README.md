
# Mod that implements OpengGL like render pipeline.

[![ru](https://img.shields.io/badge/lang-ru-green.svg)](https://github.com/leafet/FabricCustomShadersLib/blob/master/README.ru.md)

So that mod is using combination of fabric rendering api, OpenGL calls and minecraft render functions to draw diffrent kinds of VFX.

Now there is only one effect that properly works, a sphere which diapear upon approaching to it.

Shaders are loaded using Minecraft.gl methods, when vertices loading and buffers are handled by fabric renderin api, and lwjgl used to acess shader uniforms.
