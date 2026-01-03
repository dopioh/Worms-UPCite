```
.
├── ARCHITECTURE.md
├── pom.xml
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
│   │   │               │   ├── EndGameController.java
│   │   │               │   ├── ItemController.java
│   │   │               │   ├── MainMenuController.java
│   │   │               │   ├── PlayerDataController.java
│   │   │               │   └── SwingInputController.java
│   │   │               ├── FileUtile.java
│   │   │               ├── GameLauncher.java
│   │   │               └── model                                       # Modèles et logique du jeu
│   │   │                   ├── BlockGridModel.java
│   │   │                   ├── BlockHealthGrid.java
│   │   │                   ├── BlockType.java
│   │   │                   ├── camera
│   │   │                   │   └── CameraModel.java
│   │   │                   ├── CharacterModel.java
│   │   │                   ├── console
│   │   │                   │   ├── ConsoleGameLoop.java
│   │   │                   │   ├── ConsoleGameManager.java
│   │   │                   │   ├── ConsoleLauncher.java
│   │   │                   │   ├── ConsoleLogics.java
│   │   │                   │   └── PixelConverterCase.java
│   │   │                   ├── Gadget.java
│   │   │                   ├── gadgets
│   │   │                   │   └── Bumper.java
│   │   │                   ├── GameLoop.java
│   │   │                   ├── GameManager.java
│   │   │                   ├── GameStats.java
│   │   │                   ├── GroundModel.java
│   │   │                   ├── InventoryModel.java
│   │   │                   ├── io                                      # Gestion de la Sauvegarde (JSON)
│   │   │                   │   ├── CharacterData.java
│   │   │                   │   ├── CustomTeamData.java
│   │   │                   │   ├── CustomTeamManager.java
│   │   │                   │   ├── CustomTeamSaveData.java
│   │   │                   │   ├── PlayerData.java
│   │   │                   │   ├── SaveData.java
│   │   │                   │   ├── SaveLoad.java
│   │   │                   │   ├── TeamData.java
│   │   │                   │   └── WindData.java
│   │   │                   ├── Item.java
│   │   │                   ├── ItemType.java
│   │   │                   ├── MapConfig.java
│   │   │                   ├── PerlinNoise.java
│   │   │                   ├── PhysicsWorld.java
│   │   │                   ├── Projectile.java
│   │   │                   ├── SkinModel.java
│   │   │                   ├── SkinSettings.java
│   │   │                   ├── SoundPlayer.java
│   │   │                   ├── TeamInitialisation.java
│   │   │                   ├── TeamModel.java
│   │   │                   ├── TerrainGenerator.java
│   │   │                   ├── trajectories
│   │   │                   │   ├── FastTrajectory.java
│   │   │                   │   ├── HeavyTrajectory.java
│   │   │                   │   ├── NoDropTrajectory.java
│   │   │                   │   └── StandardTrajectory.java
│   │   │                   ├── TrajectoryInterface.java
│   │   │                   ├── Weapon.java
│   │   │                   ├── weapons
│   │   │                   │   ├── Ak47.java
│   │   │                   │   ├── BaseballBat.java
│   │   │                   │   ├── Grenade.java
│   │   │                   │   └── RocketLauncher.java
│   │   │                   └── WindModel.java
│   │   │               └── view                                        # Affichage graphique (Swing)
│   │   │                   ├── BlockGridView.java
│   │   │                   ├── CharacterView.java
│   │   │                   ├── console
│   │   │                   │   ├── ConsoleGamePanel.java
│   │   │                   │   └── ConsoleView.java
│   │   │                   ├── EndGameView.java
│   │   │                   ├── GamePanel.java
│   │   │                   ├── HelpOverlay.java
│   │   │                   ├── HUDOverlay.java
│   │   │                   ├── IntroCinematicView.java
│   │   │                   ├── InventoryOverlay.java
│   │   │                   ├── ItemView.java
│   │   │                   ├── LoadingBarPanel.java
│   │   │                   ├── MainMenuView.java
│   │   │                   ├── NameTagView.java
│   │   │                   ├── PauseOverlay.java
│   │   │                   ├── PillButton.java
│   │   │                   ├── ProjectileView.java
│   │   │                   ├── RoundedButton.java
│   │   │                   ├── RoundedPanel.java
│   │   │                   ├── SettingsView.java
│   │   │                   ├── ShopView.java
│   │   │                   ├── TeamView.java
│   │   │                   ├── TrajectoryView.java
│   │   │                   └── WindOverlay.java
│   │   └── resources
│   │       ├── BBHBartle-Regular.ttf
│   │       ├── GIGAWORMM.png
│   │       ├── GoogleSans-Bold.ttf
│   │       ├── Reglage_BG.png
│   │       ├── Sparkle.png
│   │       ├── Trophee.png
│   │       ├── ak.png
│   │       ├── background.png
│   │       ├── backpack.png
│   │       ├── baseballbat.png
│   │       ├── bazooka.png
│   │       ├── box.png
│   │       ├── grenade.png
│   │       ├── ground_texture.png
│   │       ├── ground_texture_old.png
│   │       ├── inventory.json
│   │       ├── nuke.png
│   │       ├── sfx
│   │       │   ├── ak47-sound.wav
│   │       │   ├── baseballbat-sound.wav
│   │       │   ├── grenade-sound.wav
│   │       │   └── rocketlauncher-sound.wav
│   │       ├── sprite_items
│   │       │   ├── ak47-sprite.png
│   │       │   ├── baseballbat-sprite.png
│   │       │   ├── grenade-sprite.png
│   │       │   ├── potion-pv-sprite.png
│   │       │   ├── rocketlauncher-sprite.png
│   │       │   └── tombe.png
│   │       ├── title.png
│   │       ├── worm.png
│   │       ├── wormNoel.png
│   │       ├── wormPinky.png
│   │       └── wormPixel.png
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
│                       │   ├── ItemControllerTerrainTest.java
│                       │   ├── ProjectileSystemTest.java
│                       │   └── WeaponSwitchTest.java
│                       ├── GroundModelTest.java
│                       ├── ImpactSystemTest.java
│                       ├── Inventory
│                       │   ├── InventoryModelTest.java
│                       │   └── InventoryOverlayTest.java
│                       ├── ItemBasicsTest.java
│                       ├── ItemControllerTest.java
│                       ├── model
│                       │   ├── BlockGridModelTest.java
│                       │   ├── BlockHealthSystemTest.java
│                       │   ├── BlockTypeTest.java
│                       │   ├── CharacterDataTest.java
│                       │   ├── GameManagerTest.java
│                       │   ├── GameStatsTest.java
│                       │   ├── RoundSystemTest.java
│                       │   ├── TeamDataTest.java
│                       │   └── WindDataTest.java
│                       ├── PlayerDataTest.java
│                       ├── ProjectileTest.java
│                       ├── TeamModelTest.java
│                       ├── view
│                       │   └── TrajectoryViewTest.java
│                       ├── weapons
│                       │   ├── Ak47Test.java
│                       │   ├── BaseballBatTest.java
│                       │   ├── GrenadeTest.java
│                       │   ├── RocketLauncherTest.java
│                       │   └── TerrainDamageWeaponsTest.java
│                       └── WeaponSystemTest.java
```