# ToDoList

基于项目 [Android Architecture Blueprints](https://github.com/googlesamples/android-architecture) 使用 MVP 模式的 Android Todo demo。

本项目旨在学习和展示 Android App 的 MVP 架构模式，应用常用的第三方库，实践常用的项目组织方式。

原有示例项目有使用 RxJava 的分支，且也有 Dagger2 的分支，但并没有 RxJava2 分支，更没有 RxJava2 + Dagger2 的分支。本项目则采用了 RxJava2 + Dagger2 + MVP 这种目前最常见的项目构建方式。

特别要注意的是，原有项目的 RxJava 分支中采用了 Sql Brite 库，令数据库操作能够轻松应用 RxJava 的相关操作。但是，Sql Brite 暂时不支持 RxJava2，故而引入 RxJava2 InterOp 实现 RxJava 和 RxJava2 结果的转化。

依赖：
- RxJava2
- RxAndroid
- Dagger2
- Butter Knife
- Sql Brite
- RxJava2 InterOp
- Debug DB