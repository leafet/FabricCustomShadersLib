
# Мод, реализующий OpengGL render pipeline (примерно).

[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/leafet/FabricCustomShadersLib/blob/master/README.md)

Итак, этот мод использует комбинацию API рендеринга fabric, вызовов OpenGL и функций рендеринга Minecraft для отрисовки различных видов VFX.

Пока есть только один эффект, который работает должным образом, сфера, которая исчезает при приближении к ней.

Шейдеры загружаются с помощью методов Minecraft.gl, когда загрузка вершин и буферов обрабатывается API рендеринга fabric, а lwjgl используется для доступа к униформам шейдеров.
