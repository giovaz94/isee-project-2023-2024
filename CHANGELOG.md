## [0.4.0](https://github.com/giovaz94/isee-project-2023-2024/compare/0.3.0...0.4.0) (2025-06-20)

### Features

* Automatic increase world shape when new blobs are added ([59c6339](https://github.com/giovaz94/isee-project-2023-2024/commit/59c633944784f45162f1a0f3d1aecc844873a465))

### Build and continuous integration

* fix dokka output directory ([ed38525](https://github.com/giovaz94/isee-project-2023-2024/commit/ed385256efe22827f943265ac4fa6de72dc4cd18))

### Refactoring

* remove suppression with nice code ([740ac8b](https://github.com/giovaz94/isee-project-2023-2024/commit/740ac8b20851264b7953a46211b05d7f914c98b9))

## [0.3.0](https://github.com/giovaz94/isee-project-2023-2024/compare/0.2.0...0.3.0) (2025-06-20)

### Features

* add line chart statistics ([166dc77](https://github.com/giovaz94/isee-project-2023-2024/commit/166dc7766ccf90527ccdca2da806493ac4e911d9))
* add max round duration and food pieces configurations ([34d87e5](https://github.com/giovaz94/isee-project-2023-2024/commit/34d87e5184110ffea513fbb3c8c561b5c74e0a13))
* add shutdown hook to shutdown mas when window is closed ([571bf5e](https://github.com/giovaz94/isee-project-2023-2024/commit/571bf5ec4719cc6de43f16ce934db6c7d96cc6e3))

### Bug Fixes

* catch errors on shutdown hooks ([cc05c3a](https://github.com/giovaz94/isee-project-2023-2024/commit/cc05c3ac6b820b388a251cfdf0daccd2a1c2c1b3))

### Build and continuous integration

* fix dokka html doc generation command ([fb4ddff](https://github.com/giovaz94/isee-project-2023-2024/commit/fb4ddff924fb1346752d2e1b97a60a0fe5295fc5))

## [0.2.0](https://github.com/giovaz94/isee-project-2023-2024/compare/0.1.0...0.2.0) (2025-06-19)

### Features

* add agent base code ([c0be49a](https://github.com/giovaz94/isee-project-2023-2024/commit/c0be49a572e91dba48880995e801003376e3333e))
* add base round impl ([e369784](https://github.com/giovaz94/isee-project-2023-2024/commit/e369784cdda41f20e5fa4962d69f5287f3e2ce35))
* add contention check ([f720ded](https://github.com/giovaz94/isee-project-2023-2024/commit/f720ded6c21e110ba7d0f3daad5b9ebbc1a2a24a))
* add energy update ([18747c1](https://github.com/giovaz94/isee-project-2023-2024/commit/18747c1de0af37ecea651b63bd2933e46ff13c7d))
* add first draft of contention rule ([3568e7b](https://github.com/giovaz94/isee-project-2023-2024/commit/3568e7bd775c336ebf9673368134669ea51691cd))
* add food collection ([2e89531](https://github.com/giovaz94/isee-project-2023-2024/commit/2e8953180d714e7e5561c41beb1cf422ef141820))
* add game loop base ([2cbc186](https://github.com/giovaz94/isee-project-2023-2024/commit/2cbc18666e29f6ac2e34dc3f81051666c280c7d0))
* add javafx simple ui ([55e8ca6](https://github.com/giovaz94/isee-project-2023-2024/commit/55e8ca63e088063b19567e2f4ca497930da6864b))
* add round visualization ([d46d933](https://github.com/giovaz94/isee-project-2023-2024/commit/d46d933a1c5e51c101aff3f45883e7594539322c))
* add World implementation along with SpawnZone ([977be09](https://github.com/giovaz94/isee-project-2023-2024/commit/977be09becb86c73d525b38b563b0c02b2a979ca))
* agent stop when food in sight ([2811818](https://github.com/giovaz94/isee-project-2023-2024/commit/28118183011c9f1209b636f90656fe0931bbb2ec))
* don't stop when food has already been locked ([9e50a97](https://github.com/giovaz94/isee-project-2023-2024/commit/9e50a973b895f19d221cd91252f827f49bf63774))
* find food ([ba71095](https://github.com/giovaz94/isee-project-2023-2024/commit/ba71095ff95e10065316b84249e781d633423ac1))
* implement bounce ([ef87b6d](https://github.com/giovaz94/isee-project-2023-2024/commit/ef87b6d966e8fff149b733ca86590742ea04eef6))
* implement death and reproduction of blobs ([97387ac](https://github.com/giovaz94/isee-project-2023-2024/commit/97387ac72eda079004c03851e427973eaee7e043))
* javafx hello world ([8f4c2e5](https://github.com/giovaz94/isee-project-2023-2024/commit/8f4c2e5094435f52a39b420c2838c04891b61576))
* launch JavaFX interface using separate threads and improve rendering using coroutines ([8d2b8b2](https://github.com/giovaz94/isee-project-2023-2024/commit/8d2b8b25cacbb0ddd939ff1b7364ab8d0766db91))
* minor adjustment on the variable on agent logic ([67eac12](https://github.com/giovaz94/isee-project-2023-2024/commit/67eac120add2b7b690c25ab507b4c45b860a7eef))
* minor adjustment on the variable on agent logic ([7ee3b3f](https://github.com/giovaz94/isee-project-2023-2024/commit/7ee3b3f411406a880e708bc1a738f28ba2fbd48b))
* **shape:** add method for testing collision with a non-placed shape ([63d86d4](https://github.com/giovaz94/isee-project-2023-2024/commit/63d86d47a11127f9a742027578426354eca78a63))
* solve conflicts ([d698d00](https://github.com/giovaz94/isee-project-2023-2024/commit/d698d000f23efd44e12e1e84713ae04e132a5771))
* update collision logic for cone ([f764491](https://github.com/giovaz94/isee-project-2023-2024/commit/f764491393e318a1bd8ba9679585b60f68a0fbda))
* **view:** add entities rendering ([aec366a](https://github.com/giovaz94/isee-project-2023-2024/commit/aec366a6e0666b48449f7bdc22411099a29ebd3d))
* world factory ([c1f9c34](https://github.com/giovaz94/isee-project-2023-2024/commit/c1f9c34ad88b1be824941bf199deff096a1802db))

### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlin.jvm to v2.1.21 ([#1](https://github.com/giovaz94/isee-project-2023-2024/issues/1)) ([60b3bab](https://github.com/giovaz94/isee-project-2023-2024/commit/60b3bab6435c6a7086ad62ee76062027d5ec3602))
* **core-deps:** update kotlinx-coroutines monorepo to v1.10.2 ([#7](https://github.com/giovaz94/isee-project-2023-2024/issues/7)) ([de33857](https://github.com/giovaz94/isee-project-2023-2024/commit/de33857777c75892f1a2ff2e18199ba907ba91a9))
* **deps:** update dependency gradle to v8.14.1 ([#8](https://github.com/giovaz94/isee-project-2023-2024/issues/8)) ([78b03ac](https://github.com/giovaz94/isee-project-2023-2024/commit/78b03ac9b0fa2aa7c07346757f5299c8738af800))
* **deps:** update dependency gradle to v8.14.2 ([#34](https://github.com/giovaz94/isee-project-2023-2024/issues/34)) ([c413750](https://github.com/giovaz94/isee-project-2023-2024/commit/c4137508dda600fbc32a1f98dfaa101d00ffb645))
* **deps:** update dependency io.mockk:mockk to v1.14.2 ([#9](https://github.com/giovaz94/isee-project-2023-2024/issues/9)) ([d1af9cc](https://github.com/giovaz94/isee-project-2023-2024/commit/d1af9ccf9050d771145c97f00f5892366f1375f7))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.10 ([#23](https://github.com/giovaz94/isee-project-2023-2024/issues/23)) ([53f6c15](https://github.com/giovaz94/isee-project-2023-2024/commit/53f6c1595e28e473f9d3aa49b86d86a4888a0232))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.11 ([#24](https://github.com/giovaz94/isee-project-2023-2024/issues/24)) ([9d5b0eb](https://github.com/giovaz94/isee-project-2023-2024/commit/9d5b0eb8fc634b2aca08ba06d7924372bf674807))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.12 ([#26](https://github.com/giovaz94/isee-project-2023-2024/issues/26)) ([8f314e6](https://github.com/giovaz94/isee-project-2023-2024/commit/8f314e6dccf7d7e8b576616240d491b86059c18f))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.13 ([#28](https://github.com/giovaz94/isee-project-2023-2024/issues/28)) ([8697610](https://github.com/giovaz94/isee-project-2023-2024/commit/8697610537ce1c280119fbae784c1cdb2d5e85ac))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.14 ([#29](https://github.com/giovaz94/isee-project-2023-2024/issues/29)) ([0012088](https://github.com/giovaz94/isee-project-2023-2024/commit/001208832982daed026552811fbbf276fda7bb33))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.15 ([#30](https://github.com/giovaz94/isee-project-2023-2024/issues/30)) ([31bea83](https://github.com/giovaz94/isee-project-2023-2024/commit/31bea835049b3eab676f25096486d27a6f85585c))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.16 ([#32](https://github.com/giovaz94/isee-project-2023-2024/issues/32)) ([b5af1a9](https://github.com/giovaz94/isee-project-2023-2024/commit/b5af1a9b00fba80adc000bebbe8ff59fade44fd8))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.17 ([#36](https://github.com/giovaz94/isee-project-2023-2024/issues/36)) ([2b8bfe6](https://github.com/giovaz94/isee-project-2023-2024/commit/2b8bfe6ce6dccc5bdd76b946ecdb7a6db0158c82))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.18 ([#38](https://github.com/giovaz94/isee-project-2023-2024/issues/38)) ([e4e749a](https://github.com/giovaz94/isee-project-2023-2024/commit/e4e749aa1a100ce366a749ce1c3fa3086abfced0))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.19 ([#39](https://github.com/giovaz94/isee-project-2023-2024/issues/39)) ([1037612](https://github.com/giovaz94/isee-project-2023-2024/commit/1037612bde628d5062428eb8273351d1086e518f))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.21 ([#40](https://github.com/giovaz94/isee-project-2023-2024/issues/40)) ([993b4b8](https://github.com/giovaz94/isee-project-2023-2024/commit/993b4b8d9c170391e716994164a68b030f0c2ae5))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.5 ([#19](https://github.com/giovaz94/isee-project-2023-2024/issues/19)) ([51c5abb](https://github.com/giovaz94/isee-project-2023-2024/commit/51c5abbd11ca310efaa8b19971fa6d9eb087729f))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.7 ([#21](https://github.com/giovaz94/isee-project-2023-2024/issues/21)) ([b74b5ea](https://github.com/giovaz94/isee-project-2023-2024/commit/b74b5ea1c7c54bc4ae8bbfbf0d84ff5d89b6fbe1))
* **deps:** update dependency it.unibo.jakta:jakta-dsl to v0.14.9 ([#22](https://github.com/giovaz94/isee-project-2023-2024/issues/22)) ([ac5b6fc](https://github.com/giovaz94/isee-project-2023-2024/commit/ac5b6fcfe2c6a90be9f35aaebf653edb1901b892))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.128 ([#3](https://github.com/giovaz94/isee-project-2023-2024/issues/3)) ([ebdc8cc](https://github.com/giovaz94/isee-project-2023-2024/commit/ebdc8cc780fc3e59c1314e266e9e558ef763adef))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.129 ([#20](https://github.com/giovaz94/isee-project-2023-2024/issues/20)) ([fd17db7](https://github.com/giovaz94/isee-project-2023-2024/commit/fd17db7948f5e8c596af1fbc02f1e0aaea7f261b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.131 ([#25](https://github.com/giovaz94/isee-project-2023-2024/issues/25)) ([f5c8a43](https://github.com/giovaz94/isee-project-2023-2024/commit/f5c8a43c0f2b30acd9a244c722d1ab2627006314))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.132 ([#31](https://github.com/giovaz94/isee-project-2023-2024/issues/31)) ([2cbad82](https://github.com/giovaz94/isee-project-2023-2024/commit/2cbad8290b52f0aa12a1d4028137bc0097278d08))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.133 ([#33](https://github.com/giovaz94/isee-project-2023-2024/issues/33)) ([d7c32e4](https://github.com/giovaz94/isee-project-2023-2024/commit/d7c32e4505062a4881b5a7499171b71dec13748d))
* **deps:** update node.js to 22.16 ([#10](https://github.com/giovaz94/isee-project-2023-2024/issues/10)) ([da3ee7b](https://github.com/giovaz94/isee-project-2023-2024/commit/da3ee7bccfa676acca0ca3dca41ce9703ad059c8))
* **deps:** update plugin com.gradle.develocity to v4.0.1 ([#4](https://github.com/giovaz94/isee-project-2023-2024/issues/4)) ([0f65b47](https://github.com/giovaz94/isee-project-2023-2024/commit/0f65b476ec70c48046a74cf81086730213e3ffef))
* **deps:** update plugin com.gradle.develocity to v4.0.2 ([#27](https://github.com/giovaz94/isee-project-2023-2024/issues/27)) ([6e1a7b8](https://github.com/giovaz94/isee-project-2023-2024/commit/6e1a7b88fb20abd806cd1bc6b939faab0362aaa1))
* **deps:** update plugin git-semantic-versioning to v5.1.3 ([#5](https://github.com/giovaz94/isee-project-2023-2024/issues/5)) ([212bafd](https://github.com/giovaz94/isee-project-2023-2024/commit/212bafd62a468bc4a925f6e711efe413a1672d31))
* **deps:** update plugin git-semantic-versioning to v5.1.4 ([#35](https://github.com/giovaz94/isee-project-2023-2024/issues/35)) ([7caab68](https://github.com/giovaz94/isee-project-2023-2024/commit/7caab68aa0be814445501e3ad18909589c2eeda5))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.24 ([#6](https://github.com/giovaz94/isee-project-2023-2024/issues/6)) ([6e8bab5](https://github.com/giovaz94/isee-project-2023-2024/commit/6e8bab5af34fcb3ae37bfd4d91da4f48af824548))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.25 ([#18](https://github.com/giovaz94/isee-project-2023-2024/issues/18)) ([78e1c81](https://github.com/giovaz94/isee-project-2023-2024/commit/78e1c810fa76496d5be077b2ebfe14761ca58092))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.26 ([#37](https://github.com/giovaz94/isee-project-2023-2024/issues/37)) ([652d8ae](https://github.com/giovaz94/isee-project-2023-2024/commit/652d8ae8fb5196d8896b6d85c488989e074665fe))
* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.10.0 ([#12](https://github.com/giovaz94/isee-project-2023-2024/issues/12)) ([131aed3](https://github.com/giovaz94/isee-project-2023-2024/commit/131aed3f0606fb6fb55917172c04edc10e5b8d3a))
* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v1 ([#15](https://github.com/giovaz94/isee-project-2023-2024/issues/15)) ([d1a00ef](https://github.com/giovaz94/isee-project-2023-2024/commit/d1a00efccbe5ed8480c1a1f89bee378970da5a07))

### Bug Fixes

* add guards to food, preventing changing status ([c778f7d](https://github.com/giovaz94/isee-project-2023-2024/commit/c778f7d6eed7f92dadf96fa3a38e1acb392c50c5))
* do not load icons everytime from file system ([0efa93f](https://github.com/giovaz94/isee-project-2023-2024/commit/0efa93fe750c2792e127ae7e829b8405dfe67e47))
* register subscribers to every round world's updates ([a59b9c8](https://github.com/giovaz94/isee-project-2023-2024/commit/a59b9c852f3e4b1e7851367f87a73c9d8b99118f))
* reporduction rule strategy ([13fc0df](https://github.com/giovaz94/isee-project-2023-2024/commit/13fc0df25eefc69df110492f0b5ac0c7997adb0b))
* resolve bug on inverse_direction function ([9939ae9](https://github.com/giovaz94/isee-project-2023-2024/commit/9939ae9f815f7670f0a9b2d41d59a50a08b40fbd))
* solve contention energy problem and graphical rendering ([972f9f0](https://github.com/giovaz94/isee-project-2023-2024/commit/972f9f08253058c2290d4a5a954560a8f8c0dcff))

### Documentation

* add agent logics ([d5e09d5](https://github.com/giovaz94/isee-project-2023-2024/commit/d5e09d5a7aef839b2515d83c8a2bbd66a9889b70))
* cleanup docs ([3b2de4f](https://github.com/giovaz94/isee-project-2023-2024/commit/3b2de4f8b3864fe0b751ce9c72a0cc37f10d4494))
* port requirements in docs ([3bb47af](https://github.com/giovaz94/isee-project-2023-2024/commit/3bb47af34abba37ee84aef743b16543fef78d5f9))

### Tests

* add test for verifying fx ui starts correctly ([8fcd5ad](https://github.com/giovaz94/isee-project-2023-2024/commit/8fcd5adc74783a896fcbd0116e950b6e243687cc))
* compatible jvm 17 api ([9d18e53](https://github.com/giovaz94/isee-project-2023-2024/commit/9d18e530cf7c63d28c8a723291ba1e89f7f44347))
* replace Thread.sleep with delay ([6d2e687](https://github.com/giovaz94/isee-project-2023-2024/commit/6d2e68755f9382a7500ef598ed97a3caeadb3f59))
* ui test now doesn't starve ([8ce0ad6](https://github.com/giovaz94/isee-project-2023-2024/commit/8ce0ad625e5575c532efc0fc5b7dccb284d9a696))

### Build and continuous integration

* activate release ([bcd075f](https://github.com/giovaz94/isee-project-2023-2024/commit/bcd075fd9f8574d97ab487db00ad4e5c21e75cc3))
* remove fatJar step ([6e912c8](https://github.com/giovaz94/isee-project-2023-2024/commit/6e912c8285e4456d627d9cdd1ab919cc5c0b6542))

### General maintenance

* **config:** migrate config renovate.json ([#16](https://github.com/giovaz94/isee-project-2023-2024/issues/16)) ([bd9b0e5](https://github.com/giovaz94/isee-project-2023-2024/commit/bd9b0e515f1841d384b5dafbe208d332ecec8ccb))
* update jakta to v0.14.4 ([1f9d18d](https://github.com/giovaz94/isee-project-2023-2024/commit/1f9d18d6597b668e780dcae09f548cca0431e8c4))

### Style improvements

* fix formattings issues ([cdccafd](https://github.com/giovaz94/isee-project-2023-2024/commit/cdccafd81bdf7ed7fa359020ed46849ba15eb31e))
* improve formatting ([a9c283a](https://github.com/giovaz94/isee-project-2023-2024/commit/a9c283a11411c1d9a8ef3c64d139737315c73122))

### Refactoring

* add agents baseline ([8fec038](https://github.com/giovaz94/isee-project-2023-2024/commit/8fec0387514406e2a2b6bdfdd680e90d5512598d))
* add draft for food spitting ([a19be46](https://github.com/giovaz94/isee-project-2023-2024/commit/a19be4641bad4dc6b71c52d2ec0cb3363aa2d466))
* add food rendering ([0ecd451](https://github.com/giovaz94/isee-project-2023-2024/commit/0ecd451be342e2217b6c37afcf09ea3530aeae1e))
* add start and stop ([cda2dd4](https://github.com/giovaz94/isee-project-2023-2024/commit/cda2dd480a9e79ab90154bc5a73058e747e67a07))
* cast instead of as ([b05e79f](https://github.com/giovaz94/isee-project-2023-2024/commit/b05e79f242f836400c6a28c2d375ccb6767d1dee))
* complete sights and blob implementation ([a0868ee](https://github.com/giovaz94/isee-project-2023-2024/commit/a0868eee0becb7cfe3f8343a6bf25efca790e391))
* dismiss engine in favor of event bus with auto-propagation of domain updates ([0798285](https://github.com/giovaz94/isee-project-2023-2024/commit/07982858fb52456d7bd3fa9de13c441586bdfb99))
* draft going home ([1e2cc88](https://github.com/giovaz94/isee-project-2023-2024/commit/1e2cc884008b1c4e7f3ce23c91ca93fd5a20af7c))
* imporve direction and simplify blob impl ([4c0abcf](https://github.com/giovaz94/isee-project-2023-2024/commit/4c0abcf755465aa6a50152dda0f2ebd18be3e1b5))
* improve code doc ([9440bac](https://github.com/giovaz94/isee-project-2023-2024/commit/9440bacbe510a6df259877b040595d50416e173d))
* improve details ([a3b414f](https://github.com/giovaz94/isee-project-2023-2024/commit/a3b414ff5097b0e16a3ede1688e6920d3b3b4e05))
* improve isFullyContainedIn ([4c3151e](https://github.com/giovaz94/isee-project-2023-2024/commit/4c3151e977859389f765da89e7393d9b900fac8e))
* improve model code ([435376c](https://github.com/giovaz94/isee-project-2023-2024/commit/435376cec2681f211ab87f10b57dd0d9324088eb))
* improve mvc arch with event bus ([76b1739](https://github.com/giovaz94/isee-project-2023-2024/commit/76b1739350ce52b87a219ab9a12351eb04495afd))
* improve mvc architecture with eb ([fd453d5](https://github.com/giovaz94/isee-project-2023-2024/commit/fd453d58f714a59e151de74479eb4c62aa4f0429))
* improve renderables with contexts ([66c8f48](https://github.com/giovaz94/isee-project-2023-2024/commit/66c8f48e42a6d70866ad9e03ee19e5ad130364ab))
* improve with extension methods ([cc3a4b0](https://github.com/giovaz94/isee-project-2023-2024/commit/cc3a4b01a01808d245f9dde68996e50dbd7ade8c))
* improve world get entities ([d486cd8](https://github.com/giovaz94/isee-project-2023-2024/commit/d486cd889a9b1382e6872c41908dba05fc2f4e38))
* inject dispatcher ([c64914f](https://github.com/giovaz94/isee-project-2023-2024/commit/c64914f0daf4113bdf8ca42e9a4a387947a851ad))
* more draft logic ([e7c3ce2](https://github.com/giovaz94/isee-project-2023-2024/commit/e7c3ce27d54e3561c018252c889c18434688a92d))
* more on rounds ([1bbc361](https://github.com/giovaz94/isee-project-2023-2024/commit/1bbc36115adae5980c1ed230dae1bd92e665de99))
* move events in model package ([1df2b2c](https://github.com/giovaz94/isee-project-2023-2024/commit/1df2b2cc527b4062e3f5abc0fbf4235f877270f7))
* no more going home ([e390473](https://github.com/giovaz94/isee-project-2023-2024/commit/e39047329b43055626393ee07e857f0ea472ac77))
* remoe json stuffs ([8a84a29](https://github.com/giovaz94/isee-project-2023-2024/commit/8a84a29f2e8026b4defa31513e51feb3ac662964))
* remove current position fact ([6868dd2](https://github.com/giovaz94/isee-project-2023-2024/commit/6868dd2fe972b46787418ae6f1d68374885b61ba))
* remove unnecessary controllers ([ac5419e](https://github.com/giovaz94/isee-project-2023-2024/commit/ac5419e06a04aae3d8973cb0e11cdb1faf89ca1c))
* rename round in forage ([3970bf2](https://github.com/giovaz94/isee-project-2023-2024/commit/3970bf25e2b62fb449d2a4a1e069c9c3400542c2))
* stop blob whenever find a food ([3827080](https://github.com/giovaz94/isee-project-2023-2024/commit/3827080a4ee0ca367c41dc77ef989429ec43452e))
* update bounce method ([54d4dc8](https://github.com/giovaz94/isee-project-2023-2024/commit/54d4dc8552274be4136c5f3d4ab088a5f6c5bee2))
* use extension methods for plans ([3d548dc](https://github.com/giovaz94/isee-project-2023-2024/commit/3d548dc6a119b35c34ba185bb83006cb49418136))
* use half of blobs foods by default ([b21ca62](https://github.com/giovaz94/isee-project-2023-2024/commit/b21ca62d46a27c507850c4c51a7078cc8cf3cece))
* use more blobs ([5cc2aec](https://github.com/giovaz94/isee-project-2023-2024/commit/5cc2aecd9dacd5eec6ca65160aa74034178dd642))
* **view:** improve view positioning ([3c334d2](https://github.com/giovaz94/isee-project-2023-2024/commit/3c334d200e35011006053e6a8729e301efc4d29f))
* **view:** separate view panels controllers to improve modularity ([3e3fbeb](https://github.com/giovaz94/isee-project-2023-2024/commit/3e3fbeb174e2d2f9d7fbd5e7d6f4aed9ada47fea))
* wire engine ([c2e830a](https://github.com/giovaz94/isee-project-2023-2024/commit/c2e830a58db2b9f2879612f88e207dadc7753ee0))
* world and sight rendering ([e6ff5c0](https://github.com/giovaz94/isee-project-2023-2024/commit/e6ff5c06d626f7a0117e73608ca96b4a92eb4b0c))
