```
.
├── ARCHITECTURE.md
├── dependency-reduced-pom.xml
├── pom.xml
├── run.log
├── src
│   ├── main
│   │   ├── java
│   │   │   └── fr
│   │   │       └── upc
│   │   │           └── g5e4
│   │   │               ├── controller                                  # Gestion des entrées (Clavier/Souris)
│   │   │               │   ├── CameraController.java
│   │   │               │   ├── CharacterController.java
│   │   │               │   ├── console
│   │   │               │   │   └── ConsoleInputController.java
│   │   │               │   ├── ItemController.java
│   │   │               │   ├── MainMenuController.java
│   │   │               │   ├── PlayerDataController.java
│   │   │               │   └── SwingInputController.java
│   │   │               ├── FileUtile.java
│   │   │               ├── GameLauncher.java
│   │   │               ├── model                                       # Modèles et logique du jeu
│   │   │               │   ├── camera
│   │   │               │   │   └── CameraModel.java
│   │   │               │   ├── CharacterModel.java
│   │   │               │   ├── console
│   │   │               │   │   ├── ConsoleGameLoop.java
│   │   │               │   │   ├── ConsoleGameManager.java
│   │   │               │   │   ├── ConsoleLauncher.java
│   │   │               │   │   ├── ConsoleLogics.java
│   │   │               │   │   └── PixelConverterCase.java
│   │   │               │   ├── Gadget.java
│   │   │               │   ├── gadgets
│   │   │               │   │   └── Bumper.java
│   │   │               │   ├── GameLoop.java
│   │   │               │   ├── GameManager.java
│   │   │               │   ├── GroundModel.java
│   │   │               │   ├── InventoryModel.java
│   │   │               │   ├── io                                       # Gestion de la Sauvegarde (JSON)
│   │   │               │   │   ├── CharacterData.java
│   │   │               │   │   ├── SaveData.java
│   │   │               │   │   └── TeamData.java
│   │   │               │   ├── Item.java
│   │   │               │   ├── ItemType.java
│   │   │               │   ├── PerlinNoise.java
│   │   │               │   ├── PhysicsWorld.java
│   │   │               │   ├── PlayerData.java
│   │   │               │   ├── Projectile.java
│   │   │               │   ├── TeamInitialisation.java
│   │   │               │   ├── TeamModel.java
│   │   │               │   ├── TerrainGenerator.java
│   │   │               │   ├── Weapon.java
│   │   │               │   └── weapons
│   │   │               │       ├── Ak47.java
│   │   │               │       ├── BaseballBat.java
│   │   │               │       ├── Grenade.java
│   │   │               │       └── RocketLauncher.java
│   │   │               ├── PerlinGameLauncher.java
│   │   │               ├── TutorialGameLauncher.java
│   │   │               └── view                                         # Affichage graphique (Swing)
│   │   │                   ├── CharacterView.java
│   │   │                   ├── console
│   │   │                   │   ├── ConsoleGamePanel.java
│   │   │                   │   └── ConsoleView.java
│   │   │                   ├── GamePanel.java
│   │   │                   ├── InventoryOverlay.java
│   │   │                   ├── ItemView.java
│   │   │                   ├── MainMenuView.java
│   │   │                   ├── NameTagView.java
│   │   │                   ├── ProjectileView.java
│   │   │                   ├── RoundedButton.java
│   │   │                   ├── TeamView.java
│   │   │                   └── TrajectoryRenderer.java
│   │   └── resources
│   │       ├── background.png
│   │       ├── backpack.png
│   │       ├── box.png
│   │       ├── GIGAWORMM.png
│   │       ├── ground_texture_old.png
│   │       ├── ground_texture.png
│   │       ├── inventory.json
│   │       ├── title.png
│   │       └── worm.png
│   └── test
│       └── java
│           └── fr
│               └── upc
│                   └── g5e4
│                       ├── BumperTest.java
│                       ├── camera
│                       │   └── CameraTest.java
│                       ├── CharacterControllerTest.java
│                       ├── CharacterModelTest.java
│                       ├── CharacterViewTest.java
│                       ├── console
│                       │   ├── ConsoleInputControllerTest.java
│                       │   └── ConsoleLogicsTest.java
│                       ├── controller
│                       │   ├── ProjectileSystemTest.java
│                       │   └── WeaponSwitchTest.java
│                       ├── GroundModelTest.java
│                       ├── Inventory
│                       │   ├── InventoryModelTest.java
│                       │   └── InventoryOverlayTest.java
│                       ├── ItemBasicsTest.java
│                       ├── ItemControllerTest.java
│                       ├── model
│                       │   └── GameManagerTest.java
│                       ├── PlayerDataTest.java
│                       ├── ProjectileTest.java
│                       ├── TeamModelTest.java
│                       ├── view
│                       │   └── TrajectoryRendererTest.java
│                       ├── weapons
│                       │   ├── Ak47Test.java
│                       │   ├── BaseballBatTest.java
│                       │   ├── GrenadeTest.java
│                       │   └── RocketLauncherTest.java
│                       └── WeaponSystemTest.java
├── target                                                              # Les .class (Pas Utile) et les Tests
│   ├── classes
│   │   ├── background.png
│   │   ├── backpack.png
│   │   ├── box.png
│   │   ├── fr
│   │   │   └── upc
│   │   │       └── g5e4
│   │   │           ├── controller
│   │   │           │   ├── CameraController.class
│   │   │           │   ├── CharacterController.class
│   │   │           │   ├── console
│   │   │           │   │   └── ConsoleInputController.class
│   │   │           │   ├── ItemController.class
│   │   │           │   ├── MainMenuController.class
│   │   │           │   ├── PlayerDataController.class
│   │   │           │   ├── SwingInputController.class
│   │   │           │   ├── SwingInputController$1.class
│   │   │           │   └── SwingInputController$2.class
│   │   │           ├── FileUtile.class
│   │   │           ├── GameLauncher.class
│   │   │           ├── GameLauncher$1.class
│   │   │           ├── GameLauncher$2.class
│   │   │           ├── GameLauncher$3.class
│   │   │           ├── model
│   │   │           │   ├── camera
│   │   │           │   │   └── CameraModel.class
│   │   │           │   ├── CharacterModel.class
│   │   │           │   ├── console
│   │   │           │   │   ├── ConsoleGameLoop.class
│   │   │           │   │   ├── ConsoleGameLoop$1.class
│   │   │           │   │   ├── ConsoleGameManager.class
│   │   │           │   │   ├── ConsoleLauncher.class
│   │   │           │   │   ├── ConsoleLogics.class
│   │   │           │   │   └── PixelConverterCase.class
│   │   │           │   ├── Gadget.class
│   │   │           │   ├── gadgets
│   │   │           │   │   └── Bumper.class
│   │   │           │   ├── GameLoop.class
│   │   │           │   ├── GameLoop$1.class
│   │   │           │   ├── GameManager.class
│   │   │           │   ├── GroundModel.class
│   │   │           │   ├── InventoryModel.class
│   │   │           │   ├── InventoryModel$InventoryJson.class
│   │   │           │   ├── InventoryModel$InventoryRoot.class
│   │   │           │   ├── io
│   │   │           │   │   ├── CharacterData.class
│   │   │           │   │   ├── SaveData.class
│   │   │           │   │   └── TeamData.class
│   │   │           │   ├── Item.class
│   │   │           │   ├── ItemType.class
│   │   │           │   ├── PerlinNoise.class
│   │   │           │   ├── PhysicsWorld.class
│   │   │           │   ├── PhysicsWorld$WaterSensorListener.class
│   │   │           │   ├── PlayerData.class
│   │   │           │   ├── Projectile.class
│   │   │           │   ├── TeamInitialisation.class
│   │   │           │   ├── TeamModel.class
│   │   │           │   ├── TerrainGenerator.class
│   │   │           │   ├── Weapon.class
│   │   │           │   └── weapons
│   │   │           │       ├── Ak47.class
│   │   │           │       ├── BaseballBat.class
│   │   │           │       ├── Grenade.class
│   │   │           │       └── RocketLauncher.class
│   │   │           ├── PerlinGameLauncher.class
│   │   │           ├── TutorialGameLauncher.class
│   │   │           └── view
│   │   │               ├── CharacterView.class
│   │   │               ├── console
│   │   │               │   ├── ConsoleGamePanel.class
│   │   │               │   └── ConsoleView.class
│   │   │               ├── GamePanel.class
│   │   │               ├── InventoryOverlay.class
│   │   │               ├── InventoryOverlay$1.class
│   │   │               ├── InventoryOverlay$2.class
│   │   │               ├── ItemView.class
│   │   │               ├── MainMenuView.class
│   │   │               ├── MainMenuView$1.class
│   │   │               ├── MainMenuView$2.class
│   │   │               ├── NameTagView.class
│   │   │               ├── ProjectileView.class
│   │   │               ├── RoundedButton.class
│   │   │               ├── RoundedButton$1.class
│   │   │               ├── TeamView.class
│   │   │               └── TrajectoryRenderer.class
│   │   ├── GIGAWORMM.png
│   │   ├── ground_texture_old.png
│   │   ├── ground_texture.png
│   │   ├── inventory.json
│   │   ├── title.png
│   │   └── worm.png
│   └── test-classes
│       └── fr
│           └── upc
│               └── g5e4
│                   ├── BumperTest.class
│                   ├── camera
│                   │   ├── CameraTest.class
│                   │   └── CameraTest$TestPersonnageModel.class
│                   ├── CharacterControllerTest.class
│                   ├── CharacterModelTest.class
│                   ├── CharacterViewTest.class
│                   ├── console
│                   │   ├── ConsoleInputControllerTest.class
│                   │   └── ConsoleLogicsTest.class
│                   ├── controller
│                   │   ├── ProjectileSystemTest.class
│                   │   ├── ProjectileSystemTest$1.class
│                   │   ├── ProjectileSystemTest$2.class
│                   │   ├── ProjectileSystemTest$3.class
│                   │   ├── ProjectileSystemTest$4.class
│                   │   └── WeaponSwitchTest.class
│                   ├── GroundModelTest.class
│                   ├── Inventory
│                   │   ├── InventoryModelTest.class
│                   │   └── InventoryOverlayTest.class
│                   ├── ItemBasicsTest.class
│                   ├── ItemBasicsTest$2.class
│                   ├── ItemBasicsTest$TestWeapon.class
│                   ├── ItemControllerTest.class
│                   ├── ItemControllerTest$FakeItemView.class
│                   ├── model
│                   │   └── GameManagerTest.class
│                   ├── PlayerDataTest.class
│                   ├── ProjectileTest.class
│                   ├── ProjectileTest$1.class
│                   ├── TeamModelTest.class
│                   ├── view
│                   │   ├── TrajectoryRendererTest.class
│                   │   └── TrajectoryRendererTest$1.class
│                   ├── weapons
│                   │   ├── Ak47Test.class
│                   │   ├── BaseballBatTest.class
│                   │   ├── GrenadeTest.class
│                   │   └── RocketLauncherTest.class
│                   └── WeaponSystemTest.class
└── testoutput.txt
```