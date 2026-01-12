```
.
│   .gitignore
│   .gitlab-ci.yml
│   .mailmap
│   AUTHORS.md
│   CHANGELOG.md
│   CONTRIBUTING.md
│   DESIGN.md
│   DIAGRAMME.md
│   LICENSE
│   README.md
│   
├───.gitlab
│   └───merge_request_templates
│           Default.md
│
├───sauvegardes
│       player_save.txt
│       sauvegarde.txt
│
└───worms
    │   ARCHITECTURE.md
    │   pom.xml
    │   run.log
    │
    └───src
        ├───main
        │   ├───java
        │   │   └───fr
        │   │       └───upc
        │   │           └───g5e4
        │   │               │   GameLauncher.java
        │   │               │
        │   │               ├───controller
        │   │               │   │   CameraController.java
        │   │               │   │   CharacterController.java
        │   │               │   │   EndGameController.java
        │   │               │   │   GadgetManager.java
        │   │               │   │   HUDOverlayController.java
        │   │               │   │   InventoryOverlayController.java
        │   │               │   │   ItemController.java
        │   │               │   │   MainMenuController.java
        │   │               │   │   PauseOverlayController.java
        │   │               │   │   PlayerDataController.java
        │   │               │   │   SettingsController.java
        │   │               │   │   SwingInputController.java
        │   │               │   │   TeamSettingsController.java
        │   │               │   │   TutorialController.java
        │   │               │   │
        │   │               │   ├───console
        │   │               │   │       ConsoleInputController.java
        │   │               │   │
        │   │               │   └───minigames
        │   │               │           WormGuessController.java
        │   │               │           WormJackController.java
        │   │               │           WormRaceController.java
        │   │               │
        │   │               ├───model
        │   │               │   │   BlockGridModel.java
        │   │               │   │   BlockHealthGridModel.java
        │   │               │   │   BlockType.java
        │   │               │   │   BuffModel.java
        │   │               │   │   CharacterModel.java
        │   │               │   │   DecorationManager.java
        │   │               │   │   DecorationModel.java
        │   │               │   │   Gadget.java
        │   │               │   │   GameLoop.java
        │   │               │   │   GameManager.java
        │   │               │   │   GameStats.java
        │   │               │   │   GroundModel.java
        │   │               │   │   InventoryModel.java
        │   │               │   │   Item.java
        │   │               │   │   ItemType.java
        │   │               │   │   MapConfig.java
        │   │               │   │   PerlinNoise.java
        │   │               │   │   PhysicsWorld.java
        │   │               │   │   ProjectileModel.java
        │   │               │   │   SkinModel.java
        │   │               │   │   SoundPlayer.java
        │   │               │   │   TeamConfig.java
        │   │               │   │   TeamInitialisation.java
        │   │               │   │   TeamModel.java
        │   │               │   │   TeamSettingsModel.java
        │   │               │   │   TerrainGenerator.java
        │   │               │   │   TrajectoryModel.java
        │   │               │   │   TutorialModel.java
        │   │               │   │   Weapon.java
        │   │               │   │   WindModel.java
        │   │               │   │
        │   │               │   ├───camera
        │   │               │   │       CameraModel.java
        │   │               │   │
        │   │               │   ├───console
        │   │               │   │       ConsoleGameLoop.java
        │   │               │   │       ConsoleGameManager.java
        │   │               │   │       ConsoleLauncher.java
        │   │               │   │       ConsoleLogics.java
        │   │               │   │       PixelConverterCase.java
        │   │               │   │
        │   │               │   ├───gadgets
        │   │               │   │       Bumper.java
        │   │               │   │       Drill.java
        │   │               │   │       PotionHeal.java
        │   │               │   │       PotionJump.java
        │   │               │   │       PotionShield.java
        │   │               │   │       PotionSpeed.java
        │   │               │   │       Teleporter.java
        │   │               │   │
        │   │               │   ├───io
        │   │               │   │       CharacterData.java
        │   │               │   │       CustomGameManager.java
        │   │               │   │       CustomTeamData.java
        │   │               │   │       PlayerData.java
        │   │               │   │       SaveData.java
        │   │               │   │       SaveLoad.java
        │   │               │   │       TeamData.java
        │   │               │   │       WindData.java
        │   │               │   │
        │   │               │   ├───minigames
        │   │               │   │       WormGuessModel.java
        │   │               │   │       WormJackModel.java
        │   │               │   │       WormRaceModel.java
        │   │               │   │
        │   │               │   ├───trajectories
        │   │               │   │       FastTrajectory.java
        │   │               │   │       HeavyTrajectory.java
        │   │               │   │       NoDropTrajectory.java
        │   │               │   │       StandardTrajectory.java
        │   │               │   │
        │   │               │   └───weapons
        │   │               │           Ak47.java
        │   │               │           BaseballBat.java
        │   │               │           Grenade.java
        │   │               │           RocketLauncher.java
        │   │               │
        │   │               ├───util
        │   │               │       ImageCache.java
        │   │               │
        │   │               └───view
        │   │                   │   BlockGridView.java
        │   │                   │   CharacterView.java
        │   │                   │   DecorationView.java
        │   │                   │   EndGameView.java
        │   │                   │   GamePanel.java
        │   │                   │   HelpOverlay.java
        │   │                   │   HUDOverlay.java
        │   │                   │   IntroCinematicView.java
        │   │                   │   InventoryOverlay.java
        │   │                   │   ItemView.java
        │   │                   │   LoadingBarPanel.java
        │   │                   │   MainMenuView.java
        │   │                   │   NameTagView.java
        │   │                   │   PauseOverlay.java
        │   │                   │   ProjectileView.java
        │   │                   │   RoundedPanel.java
        │   │                   │   SettingsView.java
        │   │                   │   TeamSettingsView.java
        │   │                   │   TeamView.java
        │   │                   │   TrajectoryView.java
        │   │                   │   TutorialView.java
        │   │                   │   WindOverlay.java
        │   │                   │
        │   │                   ├───console
        │   │                   │       ConsoleGamePanel.java
        │   │                   │       ConsoleView.java
        │   │                   │
        │   │                   ├───minigames
        │   │                   │       WormGuessView.java
        │   │                   │       WormJackView.java
        │   │                   │       WormRaceView.java
        │   │                   │
        │   │                   └───UI
        │   │                           PillButton.java
        │   │                           PillComboBox.java
        │   │                           PillTextField.java
        │   │                           RoundedButton.java
        │   │                           RoundedPanel.java
        │   │
        │   └───resources
        │       │   .gitkeep
        │       │   ak.png
        │       │   background.png
        │       │   backpack.png
        │       │   baseballbat.png
        │       │   bazooka.png
        │       │   BBHBartle-Regular.ttf
        │       │   BG.png
        │       │   BG_Jour.png
        │       │   box.png
        │       │   centercam.png
        │       │   Deco_Ground.png
        │       │   fleche-droite.png
        │       │   fleche-gauche.png
        │       │   GIGAWORMM.png
        │       │   GoogleSans-Bold.ttf
        │       │   grenade.png
        │       │   ground_texture.png
        │       │   ground_texture_old.png
        │       │   inventory.json
        │       │   luckyblock.png
        │       │   nuke.png
        │       │   parametre.png
        │       │   Reglage_BG.png
        │       │   Sparkle.png
        │       │   stick-ticket.png
        │       │   Structure.png
        │       │   title.png
        │       │   Trophee.png
        │       │   worm.png
        │       │   wormCostar.png
        │       │   wormCuisto.png
        │       │   wormGuess.png
        │       │   wormJack.png
        │       │   wormNerd.png
        │       │   wormNoel.png
        │       │   wormPinky.png
        │       │   wormPixel.png
        │       │   wormRace.png
        │       │
        │       ├───buffs
        │       │       jump_buff.png
        │       │       speed_buff.png
        │       │
        │       ├───Deco
        │       │       Bench.png
        │       │       Grass_Big.png
        │       │       Grass_Small.png
        │       │       Plant.png
        │       │       Tree1.png
        │       │       Tree2.png
        │       │
        │       ├───gadgets
        │       │       bumper-sprite.png
        │       │       drill-sprite.png
        │       │       potionheal-sprite.png
        │       │       potionjump-sprite.png
        │       │       potionshield-sprite.png
        │       │       potionspeed-sprite.png
        │       │       potion_heal.png
        │       │       potion_jump.png
        │       │       potion_shield.png
        │       │       potion_speed.png
        │       │       teleporter-sprite.png
        │       │
        │       ├───Map_shots
        │       │       Large_flat.png
        │       │       Large_islands.png
        │       │       Large_mountains.png
        │       │       Large_standard.png
        │       │       Medium_flat.png
        │       │       Medium_islands.png
        │       │       Medium_mountains.png
        │       │       Medium_standard.png
        │       │       Small_flat.png
        │       │       Small_islands.png
        │       │       Small_mountains.png
        │       │       Small_standard.png
        │       │
        │       ├───sfx
        │       │       ak47-sound.wav
        │       │       baseballbat-sound.wav
        │       │       breakblock.wav
        │       │       depop.wav
        │       │       drill-break.wav
        │       │       drill-place.wav
        │       │       game-music.wav
        │       │       grenade-sound.wav
        │       │       menu-music.wav
        │       │       potionheal-sound.wav
        │       │       potionjump-sound.wav
        │       │       potionshield-sound.wav
        │       │       potionspeed-sound.wav
        │       │       rocketlauncher-sound.wav
        │       │       teleporter-sound.wav
        │       │
        │       ├───sprite_card
        │       │       1-C.png
        │       │       1-D.png
        │       │       1-H.png
        │       │       1-P.png
        │       │       10-C.png
        │       │       10-D.png
        │       │       10-H.png
        │       │       10-P.png
        │       │       11-C.png
        │       │       11-D.png
        │       │       11-H.png
        │       │       11-P.png
        │       │       12-C.png
        │       │       12-D.png
        │       │       12-H.png
        │       │       12-P.png
        │       │       13-C.png
        │       │       13-D.png
        │       │       13-H.png
        │       │       13-P.png
        │       │       2-C.png
        │       │       2-D.png
        │       │       2-H.png
        │       │       2-P.png
        │       │       3-C.png
        │       │       3-D.png
        │       │       3-H.png
        │       │       3-P.png
        │       │       4-C.png
        │       │       4-D.png
        │       │       4-H.png
        │       │       4-P.png
        │       │       5-C.png
        │       │       5-D.png
        │       │       5-H.png
        │       │       5-P.png
        │       │       6-C.png
        │       │       6-D.png
        │       │       6-H.png
        │       │       6-P.png
        │       │       7-C.png
        │       │       7-D.png
        │       │       7-H.png
        │       │       7-P.png
        │       │       8-C.png
        │       │       8-D.png
        │       │       8-H.png
        │       │       8-P.png
        │       │       9-C.png
        │       │       9-D.png
        │       │       9-H.png
        │       │       9-P.png
        │       │       BACK.png
        │       │       JOKER.png
        │       │
        │       └───sprite_items
        │               ak47-sprite.png
        │               baseballbat-sprite.png
        │               grenade-sprite.png
        │               rocketlauncher-sprite.png
        │               tombe.png
        │
        └───test
            └───java
                └───fr
                    └───upc
                        └───g5e4
                            ├───controller
                            │   │   CharacterControllerTest.java
                            │   │   ConsoleInputControllerTest.java
                            │   │   ConsoleLogicsTest.java
                            │   │   HUDOverlayControllerTest.java
                            │   │   InventoryOverlayControllerTest.java
                            │   │   ItemControllerTerrainTest.java
                            │   │   ItemControllerTest.java
                            │   │   PauseOverlayControllerTest.java
                            │   │   ProjectileSystemTest.java
                            │   │   TeamSettingsControllerTest.java
                            │   │   TutorialControllerTest.java
                            │   │   WeaponSwitchTest.java
                            │   │
                            │   └───minigames
                            │           WormGuessControllerTest.java
                            │           WormJackControllerTest.java
                            │           WormRaceControllerTest.java
                            │
                            ├───integration
                            │       TeamSettingsIntegrationTest.java
                            │       WormGuessIntegrationTest.java
                            │       WormJackIntegrationTest.java
                            │       WormRaceIntegrationTest.java
                            │
                            ├───model
                            │   │   BlockGridModelTest.java
                            │   │   BlockHealthSystemTest.java
                            │   │   BlockTypeTest.java
                            │   │   CameraTest.java
                            │   │   CharacterDataTest.java
                            │   │   CharacterModelTest.java
                            │   │   DecorationManagerTest.java
                            │   │   GadgetSystemTest.java
                            │   │   GameManagerTest.java
                            │   │   GameStatsTest.java
                            │   │   GroundModelTest.java
                            │   │   InventoryModelTest.java
                            │   │   PlayerDataTest.java
                            │   │   RoundSystemTest.java
                            │   │   SaveDataTest.java
                            │   │   SaveLoadTest.java
                            │   │   SoundPlayerTest.java
                            │   │   TeamDataTest.java
                            │   │   TeamModelTest.java
                            │   │   TeamSettingsModelTest.java
                            │   │   TutorialModelTest.java
                            │   │   WindDataTest.java
                            │   │   windModelTest.java
                            │   │
                            │   ├───gadget
                            │   │       BuffEffectTest.java
                            │   │       ItemBasicsTest.java
                            │   │
                            │   ├───minigames
                            │   │       WormGuessModelTest.java
                            │   │       WormJackModelTest.java
                            │   │       WormRaceModelTest.java
                            │   │
                            │   └───weapons
                            │           Ak47Test.java
                            │           BaseballBatTest.java
                            │           GrenadeTest.java
                            │           ImpactSystemTest.java
                            │           ProjectileTest.java
                            │           RocketLauncherTest.java
                            │           TerrainDamageWeaponsTest.java
                            │           WeaponSystemTest.java
                            │
                            └───view
                                │   CharacterViewTest.java
                                │   HelpOverlayTest.java
                                │   HUDOverlayTest.java
                                │   InventoryOverlayTest.java
                                │   NameTagTimerTest.java
                                │   PauseOverlayTest.java
                                │   TeamSettingsViewTest.java
                                │   TrajectoryViewTest.java
                                │   TutorialViewTest.java
                                │   WindOverlayTest.java
                                │
                                └───minigames
                                        WormGuessViewTest.java
                                        WormJackViewTest.java
                                        WormRaceViewTest.java
```