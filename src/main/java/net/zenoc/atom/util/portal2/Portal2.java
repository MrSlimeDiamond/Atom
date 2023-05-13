package net.zenoc.atom.util.portal2;

import java.util.ArrayList;
import java.util.Optional;

public class Portal2 {
    public static final ArrayList<P2Map> maps = new ArrayList<>();

    static {
        // this was not fun to do

        maps.add(new P2Map("sp_a1_intro1"                , "Container Ride",       1, false, 62761));
        maps.add(new P2Map("sp_a1_intro2"                , "Portal Carousel",      1, false, 62758));
        maps.add(new P2Map("sp_a1_intro3"                , "Portal Gun",           1, false, 47458));
        maps.add(new P2Map("sp_a1_intro4"                , "Smooth Jazz",          1, false, 47455));
        maps.add(new P2Map("sp_a1_intro5"                , "Cube Momentum",        1, false, 47452));
        maps.add(new P2Map("sp_a1_intro6"                , "Future Starter",       1, false, 47106));
        maps.add(new P2Map("sp_a1_intro7"                , "Secret Panel",         1, false, 62763));
        maps.add(new P2Map("sp_a1_wakeup"                , "Wakeup",               1, false, 62759));
        maps.add(new P2Map("sp_a2_intro"                 , "Incinerator",          1, false, 47735));
        maps.add(new P2Map("sp_a2_laser_intro"           , "Laser Intro",          2, false, 62765));
        maps.add(new P2Map("sp_a2_laser_stairs"          , "Laser Stairs",         2, false, 47736));
        maps.add(new P2Map("sp_a2_dual_lasers"           , "Dual Lasers",          2, false, 47738));
        maps.add(new P2Map("sp_a2_laser_over_goo"        , "Laser Over Goo",       2, false, 47742));
        maps.add(new P2Map("sp_a2_catapult_intro"        , "Catapult Intro",       2, false, 62767));
        maps.add(new P2Map("sp_a2_trust_fling"           , "Trust Fling",          2, false, 47744));
        maps.add(new P2Map("sp_a2_pit_flings"            , "Pit Flings",           2, false, 47465));
        maps.add(new P2Map("sp_a2_fizzler_intro"         , "Fizzler Intro",        2, false, 47746));
        maps.add(new P2Map("sp_a2_sphere_peek"           , "Ceiling Catapult",     3, false, 47748));
        maps.add(new P2Map("sp_a2_ricochet"              , "Ricochet",             3, false, 47751));
        maps.add(new P2Map("sp_a2_bridge_intro"          , "Bridge Intro",         3, false, 47752));
        maps.add(new P2Map("sp_a2_bridge_the_gap"        , "Bridge The Gap",       3, false, 47755));
        maps.add(new P2Map("sp_a2_turret_intro"          , "Turret Intro",         3, false, 47756));
        maps.add(new P2Map("sp_a2_laser_relays"          , "Laser Relays",         3, false, 47759));
        maps.add(new P2Map("sp_a2_turret_blocker"        , "Turret Blocker",       3, false, 47760));
        maps.add(new P2Map("sp_a2_laser_vs_turret"       , "Laser vs Turret",      3, false, 47763));
        maps.add(new P2Map("sp_a2_pull_the_rug"          , "Pull The Rug",         3, false, 47764));
        maps.add(new P2Map("sp_a2_column_blocker"        , "Column Blocker",       4, false, 47766));
        maps.add(new P2Map("sp_a2_laser_chaining"        , "Laser Chaining",       4, false, 47768));
        maps.add(new P2Map("sp_a2_triple_laser"          , "Triple Laser",         4, false, 47770));
        maps.add(new P2Map("sp_a2_bts1"                  , "Jailbreak",            4, false, 47773));
        maps.add(new P2Map("sp_a2_bts2"                  , "Escape",               4, false, 47774));
        maps.add(new P2Map("sp_a2_bts3"                  , "Turret Factory",       5, false, 47776));
        maps.add(new P2Map("sp_a2_bts4"                  , "Turret Sabotage",      5, false, 47779));
        maps.add(new P2Map("sp_a2_bts5"                  , "Neurotoxin Sabotage",  5, false, 47780));
        maps.add(new P2Map("sp_a2_bts6"                  , "Tube Ride",            5, false, -1));
        maps.add(new P2Map("sp_a2_core"                  , "Core",                 5, false, 62771));
        maps.add(new P2Map("sp_a3_00"                    , "Long Fall",            6, false, -1));
        maps.add(new P2Map("sp_a3_01"                    , "Underground",          6, false, 47783));
        maps.add(new P2Map("sp_a3_03"                    , "Cave Johnson",         6, false, 47784));
        maps.add(new P2Map("sp_a3_jump_intro"            , "Repulsion Intro",      6, false, 47787));
        maps.add(new P2Map("sp_a3_bomb_flings"           , "Bomb Flings",          6, false, 47468));
        maps.add(new P2Map("sp_a3_crazy_box"             , "Crazy Box",            6, false, 47469));
        maps.add(new P2Map("sp_a3_transition01"          , "PotatOS",              6, false, 47472));
        maps.add(new P2Map("sp_a3_speed_ramp"            , "Propulsion Intro",     7, false, 47791));
        maps.add(new P2Map("sp_a3_speed_flings"          , "Propulsion Flings",    7, false, 47793));
        maps.add(new P2Map("sp_a3_portal_intro"          , "Conversion Intro",     7, false, 47795));
        maps.add(new P2Map("sp_a3_end"                   , "Three Gels",           7, false, 47798));
        maps.add(new P2Map("sp_a4_intro"                 , "Test",                 8, false, 88350));
        maps.add(new P2Map("sp_a4_tb_intro"              , "Funnel Intro",         8, false, 47800));
        maps.add(new P2Map("sp_a4_tb_trust_drop"         , "Ceiling Button",       8, false, 47802));
        maps.add(new P2Map("sp_a4_tb_wall_button"        , "Wall Button",          8, false, 47804));
        maps.add(new P2Map("sp_a4_tb_polarity"           , "Polarity",             8, false, 47806));
        maps.add(new P2Map("sp_a4_tb_catch"              , "Funnel Catch",         8, false, 47808));
        maps.add(new P2Map("sp_a4_stop_the_box"          , "Stop The Box",         8, false, 47811));
        maps.add(new P2Map("sp_a4_laser_catapult"        , "Laser Catapult",       8, false, 47813));
        maps.add(new P2Map("sp_a4_laser_platform"        , "Laser Platform",       8, false, 47815));
        maps.add(new P2Map("sp_a4_speed_tb_catch"        , "Propulsion Catch",     8, false, 47817));
        maps.add(new P2Map("sp_a4_jump_polarity"         , "Repulsion Polarity",   8, false, 47819));
        maps.add(new P2Map("sp_a4_finale1"               , "Finale 1",             9, false, 62776));
        maps.add(new P2Map("sp_a4_finale2"               , "Finale 2",             9, false, 47821));
        maps.add(new P2Map("sp_a4_finale3"               , "Finale 3",             9, false, 47824));
        maps.add(new P2Map("sp_a4_finale4"               , "Finale 4",             9, false, 47456));
        maps.add(new P2Map("mp_coop_start"               , "Calibration",          0, true,  -1));
        maps.add(new P2Map("mp_coop_lobby_3"             , "Hub",                  0, true,  -1));
        maps.add(new P2Map("mp_coop_doors"               , "Doors",                1, true,  47741));
        maps.add(new P2Map("mp_coop_race_2"              , "Buttons",              1, true,  47825));
        maps.add(new P2Map("mp_coop_laser_2"             , "Lasers",               1, true,  47828));
        maps.add(new P2Map("mp_coop_rat_maze"            , "Rat Maze",             1, true,  47829));
        maps.add(new P2Map("mp_coop_laser_crusher"       , "Laser Crusher",        1, true,  45467));
        maps.add(new P2Map("mp_coop_teambts"             , "Behind The Scenes",    1, true,  46362));
        maps.add(new P2Map("mp_coop_fling_3"             , "Flings",               2, true,  47831));
        maps.add(new P2Map("mp_coop_infinifling_train"   , "Infinifling",          2, true,  47833));
        maps.add(new P2Map("mp_coop_come_along"          , "Team Retrieval",       2, true,  47835));
        maps.add(new P2Map("mp_coop_fling_1"             , "Vertical Flings",      2, true,  47837));
        maps.add(new P2Map("mp_coop_catapult_1"          , "Catapults",            2, true,  47840));
        maps.add(new P2Map("mp_coop_multifling_1"        , "Multifling",           2, true,  47841));
        maps.add(new P2Map("mp_coop_fling_crushers"      , "Fling Crushers",       2, true,  47844));
        maps.add(new P2Map("mp_coop_fan"                 , "Industrial Fan",       2, true,  47845));
        maps.add(new P2Map("mp_coop_wall_intro"          , "Cooperative Bridges",  3, true,  47848));
        maps.add(new P2Map("mp_coop_wall_2"              , "Bridge Swap",          3, true,  47849));
        maps.add(new P2Map("mp_coop_catapult_wall_intro" , "Fling Block",          3, true,  47854));
        maps.add(new P2Map("mp_coop_wall_block"          , "Catapult Block",       3, true,  47856));
        maps.add(new P2Map("mp_coop_catapult_2"          , "Bridge Fling",         3, true,  47858));
        maps.add(new P2Map("mp_coop_turret_walls"        , "Turret Walls",         3, true,  47861));
        maps.add(new P2Map("mp_coop_turret_ball"         , "Turret Assassin",      3, true,  52642));
        maps.add(new P2Map("mp_coop_wall_5"              , "Bridge Testing",       3, true,  52660));
        maps.add(new P2Map("mp_coop_tbeam_redirect"      , "Cooperative Funnels",  4, true,  52662));
        maps.add(new P2Map("mp_coop_tbeam_drill"         , "Funnel Drill",         4, true,  52663));
        maps.add(new P2Map("mp_coop_tbeam_catch_grind_1" , "Funnel Catch Coop",    4, true,  52665));
        maps.add(new P2Map("mp_coop_tbeam_laser_1"       , "Funnel Laser",         4, true,  52667));
        maps.add(new P2Map("mp_coop_tbeam_polarity"      , "Cooperative Polarity", 4, true,  52671));
        maps.add(new P2Map("mp_coop_tbeam_polarity2"     , "Funnel Hop",           4, true,  52687));
        maps.add(new P2Map("mp_coop_tbeam_polarity3"     , "Advanced Polarity",    4, true,  52689));
        maps.add(new P2Map("mp_coop_tbeam_maze"          , "Funnel Maze",          4, true,  52691));
        maps.add(new P2Map("mp_coop_tbeam_end"           , "Turret Warehouse",     4, true,  52777));
        maps.add(new P2Map("mp_coop_paint_come_along"    , "Repulsion Jumps",      5, true,  52694));
        maps.add(new P2Map("mp_coop_paint_redirect"      , "Double Bounce",        5, true,  52711));
        maps.add(new P2Map("mp_coop_paint_bridge"        , "Bridge Repulsion",     5, true,  52714));
        maps.add(new P2Map("mp_coop_paint_walljumps"     , "Wall Repulsion",       5, true,  52715));
        maps.add(new P2Map("mp_coop_paint_speed_fling"   , "Propulsion Crushers",  5, true,  52717));
        maps.add(new P2Map("mp_coop_paint_red_racer"     , "Turret Ninja",         5, true,  52735));
        maps.add(new P2Map("mp_coop_paint_speed_catch"   , "Propulsion Retrieval", 5, true,  52738));
        maps.add(new P2Map("mp_coop_paint_longjump_intro", "Vault Entrance",       5, true,  52740));
        maps.add(new P2Map("mp_coop_separation_1"        , "Separation",           6, true,  49341));
        maps.add(new P2Map("mp_coop_tripleaxis"          , "Triple Axis",          6, true,  49343));
        maps.add(new P2Map("mp_coop_catapult_catch"      , "Catapult Catch",       6, true,  49345));
        maps.add(new P2Map("mp_coop_2paints_1bridge"     , "Bridge Gels",          6, true,  49347));
        maps.add(new P2Map("mp_coop_paint_conversion"    , "Maintenance",          6, true,  49349));
        maps.add(new P2Map("mp_coop_bridge_catch"        , "Bridge Catch",         6, true,  49351));
        maps.add(new P2Map("mp_coop_laser_tbeam"         , "Double Lift",          6, true,  52757));
        maps.add(new P2Map("mp_coop_paint_rat_maze"      , "Gel Maze",             6, true,  52759));
        maps.add(new P2Map("mp_coop_paint_crazy_box"     , "Crazier Box",          6, true,  48287));
    }

    public static Optional<P2Map> getMapByFormattedName(String formattedName) {
        for (P2Map map : maps) {
            if (map.getFormattedName().equals(formattedName)) {
                return Optional.of(map);
            }
        }
        // in case an invalid map is requested, just return
        return Optional.empty();
    }

    public static Optional<P2Map> getMapByID(int id) {
        for (P2Map map : maps) {
            if (map.getSteamMapID() == id) {
                return Optional.of(map);
            }
        }
        return Optional.empty();
    }
}
