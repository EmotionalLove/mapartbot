package me.zeroeightsix.discord.schematic;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.jnbt.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MapConverter {
    private final String version = "0.0.13";
    private boolean allowLapis = true;
    private boolean allowEmerald = true;
    private boolean allowCobweb = true;
    private boolean allowBed = false;
    private boolean allowTNT = true;
    private boolean allowBrewingStand = true;
    private boolean allowGoldPlates = true;
    private boolean allowWater = true;
    private boolean forceDiorite = false;
    private int supportBlockId = 57;
    private int supportBlockData = 0;
    private boolean force2D = false;
    private boolean grayscale = false;
    private boolean dithering = true;
    private String pathToOutputFolder;
    private String pathToInputImage;
    private final int numberOfBaseColors = 51;
    private int[][][] baseColorsRGB = new int[51][3][3];
    private int[][][] baseColorsLAB = new int[51][3][3];
    private boolean[][] allowColor = new boolean[51][3];
    private int[] bestBlockId = new int[]{2, 24, 26, 46, 79, 117, 18, 35, 82, 5, 1, 8, 5, 17, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 147, 168, 22, 133, 5, 87, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159};
    private int[] bestBlockData = new int[]{0, 0, 0, 0, 0, 0, 4, 0, 0, 3, 0, 0, 0, 6, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 0, 0, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private boolean[] bestBlockNeedsSupport = new boolean[]{false, false, true, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    private int unplacedBeds = 0;

    public MapConverter() {
    }

    public void main(String[] var0) {
        parseArguments(var0);
        System.out.println("Converting image " + pathToInputImage + ":");
        System.out.println("Loading and preparing image...");
        initColorData();
        BufferedImage var1 = loadImage(pathToInputImage);
        int var2 = (int) (128.0D * Math.ceil((double) var1.getWidth() / 128.0D));
        int var3 = (int) (128.0D * Math.ceil((double) var1.getHeight() / 128.0D));
        BufferedImage var4 = new BufferedImage(var2, var3, 1);
        var4.getGraphics().drawImage(var1, 0, 0, (ImageObserver) null);
        var1 = new BufferedImage(var2, var3, 1);
        var1.getGraphics().drawImage(var4, 0, 0, (ImageObserver) null);
        System.out.println("Generating color palette...");
        saveColorPaletteImage(pathToOutputFolder + "png/colorPalette.png");
        System.out.println("Reducing colors...");
        int[][] var5 = getMapColorMatrix(var4, var1, allowColor, true);
        int[][] var6 = new int[var2][var3];
        int[][] var7 = new int[var2][var3];

        int var8;
        int var9;
        for (var8 = 0; var8 < var2; ++var8) {
            for (var9 = 0; var9 < var3; ++var9) {
                var7[var8][var9] = var5[var8][var9] / 3;
                var6[var8][var9] = -1 * (var5[var8][var9] % 3 - 1);
            }
        }

        System.out.println("Splitting everything into square 128px sections...");
        var8 = var2 / 128;
        var9 = var3 / 128;
        BufferedImage[][] var10 = new BufferedImage[var8][var9];
        BufferedImage[][] var11 = new BufferedImage[var8][var9];
        int[][][][] var12 = new int[var8][var9][128][128];
        int[][][][] var13 = new int[var8][var9][128][128];

        int var14;
        int var15;
        for (var14 = 0; var14 < var8; ++var14) {
            for (var15 = 0; var15 < var9; ++var15) {
                var10[var14][var15] = new BufferedImage(128, 128, 1);
                var11[var14][var15] = new BufferedImage(128, 128, 1);

                for (int var16 = 0; var16 < 128; ++var16) {
                    for (int var17 = 0; var17 < 128; ++var17) {
                        var10[var14][var15].setRGB(var16, var17, var1.getRGB(var14 * 128 + var16, var15 * 128 + var17));
                        var11[var14][var15].setRGB(var16, var17, var4.getRGB(var14 * 128 + var16, var15 * 128 + var17));
                        var12[var14][var15][var16][var17] = var7[var14 * 128 + var16][var15 * 128 + var17];
                        var13[var14][var15][var16][var17] = var6[var14 * 128 + var16][var15 * 128 + var17];
                    }
                }
            }
        }

        System.out.println("Generating and saving terrain data (schematics)...");

        for (var14 = 0; var14 < var8; ++var14) {
            for (var15 = 0; var15 < var9; ++var15) {
                int[][][][] var22 = createTerrainFromNormalizedData(var13[var14][var15], var12[var14][var15], var10[var14][var15], var11[var14][var15], var4, var14, var15);
                saveSchematicFromTerrain(var22, pathToOutputFolder + "schematic/section." + var14 + "." + var15 + ".schematic");
                HashMap var23 = countSpecialBlocks(var22);
                boolean var18 = false;
                Iterator var19 = var23.keySet().iterator();

                while (var19.hasNext()) {
                    String var20 = (String) var19.next();
                    int var21 = (Integer) var23.get(var20);
                    if (var21 > 0) {
                        if (var18) {
                            System.out.print(", " + var21 + " " + var20);
                        } else {
                            var18 = true;
                            System.out.print("\tSection " + var14 + "." + var15 + " contains " + var21 + " " + var20);
                        }
                    }
                }

                if (var18) {
                    System.out.println(".");
                }
            }
        }

        System.out.println("Saving image previews...");
        saveImage(var4, pathToOutputFolder + "png/completeImage.png");

        for (var14 = 0; var14 < var8; ++var14) {
            for (var15 = 0; var15 < var9; ++var15) {
                saveImage(var11[var14][var15], pathToOutputFolder + "png/section" + var14 + "." + var15 + ".png");
            }
        }

        System.out.println("===================================================");
        if (unplacedBeds > 0) {
            System.out.println("Warning: You have chosen to disable cobwebs without disabling beds.");
            System.out.println(unplacedBeds + " pixels that were meant to be the color of beds have been replaced with worse alternatives.");
        }

        System.out.println("Note: schematics are 130x130 in footprint, use their glass floor as a reference for map alignment.");
        System.out.println("Don't place any blocks touching the 128x128 map area, except for those the .schematic places there. Have fun.");
    }

    public static void compareMapFiles(String var0, String var1) {
        byte[] var2 = null;
        byte[] var3 = null;

        try {
            File var4 = new File(var0);
            File var5 = new File(var1);
            FileInputStream var6 = new FileInputStream(var4);
            NBTInputStream var7 = new NBTInputStream(var6);
            CompoundTag var8 = (CompoundTag) var7.readTag();
            var7.close();
            CompoundTag var9 = (CompoundTag) var8.getValue().get("data");
            ByteArrayTag var10 = (ByteArrayTag) var9.getValue().get("colors");
            var3 = var10.getValue();
            FileInputStream var11 = new FileInputStream(var5);
            NBTInputStream var12 = new NBTInputStream(var11);
            CompoundTag var13 = (CompoundTag) var12.readTag();
            var12.close();
            CompoundTag var14 = (CompoundTag) var13.getValue().get("data");
            ByteArrayTag var15 = (ByteArrayTag) var14.getValue().get("colors");
            var2 = var15.getValue();
        } catch (Exception var16) {
            System.out.println("Something went wrong:");
            var16.printStackTrace();
        }

        int var17 = 0;

        for (int var18 = 0; var18 < 128; ++var18) {
            for (int var19 = 0; var19 < 128; ++var19) {
                byte var20 = var2[var18 + var19 * 128];
                byte var21 = var3[var18 + var19 * 128];
                if (var20 != var21) {
                    ++var17;
                    System.out.println("Difference found! x: " + var18 + "\ty: " + var19);
                }
            }
        }

        System.out.println(var17 + " different pixels found.");
    }

    private void parseArguments(String[] var0) {
        String[] var1 = var0;
        int var2 = var0.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            if (var4.equals("-h") || var4.equals("--help") || var4.equals("-help") || var4.equals("--h")) {
                printHelpAndQuit();
            }

            if (var4.equals("--about")) {
                printAboutAndQuit();
            }
        }

        if (var0.length > 0) {
            pathToInputImage = var0[0];
        } else {
            System.out.println("Invalid arguments. Use --help for help.");
            System.exit(0);
        }

        boolean var9 = false;
        if (var0.length > 1 && !var0[1].startsWith("-")) {
            pathToOutputFolder = var0[1];
            var9 = true;
        }

        if (!var9) {
            var2 = pathToInputImage.lastIndexOf(47);
            pathToOutputFolder = pathToInputImage.substring(0, var2 + 1) + "out/";
        }

        if (!pathToOutputFolder.endsWith("/")) {
            pathToOutputFolder = pathToOutputFolder + "/";
        }

        File var10 = new File(pathToOutputFolder + "png");
        File var11 = new File(pathToOutputFolder + "schematic");
        var10.mkdirs();
        var11.mkdir();

        for (int var12 = 0; var12 < var0.length; ++var12) {
            if (var0[var12].equals("--nolapis")) {
                allowLapis = false;
            }

            if (var0[var12].equals("--noemerald")) {
                allowEmerald = false;
            }

            if (var0[var12].equals("--nocobweb")) {
                allowCobweb = false;
            }

            if (var0[var12].equals("--nobed")) {
                allowBed = false;
            }

            if (var0[var12].equals("--notnt")) {
                allowTNT = false;
            }

            if (var0[var12].equals("--nobrewstand")) {
                allowBrewingStand = false;
            }

            if (var0[var12].equals("--nogoldplate")) {
                allowGoldPlates = false;
            }

            if (var0[var12].equals("--nowater")) {
                allowWater = false;
            }

            if (var0[var12].equals("--usediorite")) {
                forceDiorite = true;
            }

            int var5;
            if (var0[var12].equals("--supportid")) {
                if (var0.length > var12 + 1) {
                    var5 = 0;

                    try {
                        var5 = Integer.parseInt(var0[var12 + 1]);
                    } catch (NumberFormatException var7) {
                        System.out.println("Support Block ID must be valid block ID!");
                        System.exit(0);
                    }

                    if (var5 < 0 || var5 > 4095) {
                        System.out.println("Support Block ID must be valid block ID!");
                        System.exit(0);
                    }

                    supportBlockId = var5;
                    ++var12;
                } else {
                    System.out.println("Invalid arguments. Use --help for help.");
                    System.exit(0);
                }
            }

            if (var0[var12].equals("--supportdata")) {
                if (var0.length > var12 + 1) {
                    var5 = 0;

                    try {
                        var5 = Integer.parseInt(var0[var12 + 1]);
                    } catch (NumberFormatException var8) {
                        System.out.println("Support Block data value must be valid data value!");
                        System.exit(0);
                    }

                    if (var5 < 0 || var5 > 15) {
                        System.out.println("Support Block data value must be valid data value!");
                        System.exit(0);
                    }

                    supportBlockData = var5;
                    ++var12;
                } else {
                    System.out.println("Invalid arguments. Use --help for help.");
                    System.exit(0);
                }
            }

            if (var0[var12].equals("--force2d")) {
                force2D = true;
            }

            if (var0[var12].equals("--greyscale")) {
                grayscale = true;
            }

            if (var0[var12].equals("--nodither")) {
                dithering = false;
            }
        }

    }

    private void printHelpAndQuit() {
        System.out.println("Usage: java -jar MapConverter.jar infile [outpath] [options]");
        System.out.println("\tIf not specified, outpath will be \"out\" in the same folder as infile");
        System.out.println("\tWarning: Contents of outpath folder may be overwritten.");
        System.out.println("\nGeneral options:");
        System.out.println("-h or --help\t\tPrint this help text");
        System.out.println("--about\t\t\tPrint info");
        System.out.println("--greyscale\t\tOnly use grey colors");
        System.out.println("--nodither\t\tDisable dithering");
        System.out.println("--force2d\t\tDon't allow height differences in schematics");
        System.out.println("\t\t\t  Note: Restricts usable colors by 2/3");
        System.out.println("--supportid id\t\tNumerical support block id (for beds...)");
        System.out.println("--supportdata data\tSupport block data value");
        System.out.println("\t\t\t  Note: Support block needs to be a solid block.");
        System.out.println("\nBlock options:");
        System.out.println("--nolapis\t\tDisable Lapis blocks");
        System.out.println("--noemerald\t\tDisable Emerald blocks");
        System.out.println("--nocobweb\t\tDisable Cobwebs");
        System.out.println("--nobed\t\t\tDisable Beds (Beds are permanently disabled in this version)");
        System.out.println("--notnt\t\t\tDisable TNT");
        System.out.println("--nobrewstand\t\tDisable Brewing Stands");
        System.out.println("--nogoldplate\t\tDisable (golden) Weighted Pressure Plates");
        System.out.println("--nowater\t\tDisable Water");
        System.out.println("--usediorite\t\tUse Diorite instead of Birch Logs");
        System.exit(0);
    }

    private void printAboutAndQuit() {
        System.out.println("Made by /u/redstonehelper on reddit");
        System.out.println("Some code borrowed from f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm");
        System.out.println("Requires JNBT 1.1: jnbt.sourceforge.net");
        System.out.println("Version 0.0.13");
        System.exit(0);
    }

    private void initColorData() {
        int[] var0 = new int[]{8368696, 16247203, 13092807, 16711680, 10526975, 10987431, 31744, 16777215, 10791096, 9923917, 7368816, 4210943, 9402184, 16776437, 14188339, 11685080, 6724056, 15066419, 8375321, 15892389, 5000268, 10066329, 5013401, 8339378, 3361970, 6704179, 6717235, 10040115, 1644825, 16445005, 6085589, 4882687, 55610, 8476209, 7340544, 13742497, 10441252, 9787244, 7367818, 12223780, 6780213, 10505550, 3746083, 8874850, 5725276, 8014168, 4996700, 4993571, 5001770, 9321518, 2430480};
        int[] var1 = new int[]{2, 5, 7, 10, 13, 20, 21, 28};

        int var2;
        int var3;
        for (var2 = 0; var2 < 51; ++var2) {
            for (var3 = 0; var3 < 3; ++var3) {
                short var4 = 0;
                if (var3 == 0) {
                    var4 = 180;
                }

                if (var3 == 1) {
                    var4 = 220;
                }

                if (var3 == 2) {
                    var4 = 255;
                }

                int[] var5 = getRGBtriple(var0[var2]);
                var5[0] = var5[0] * var4 / 255;
                var5[1] = var5[1] * var4 / 255;
                var5[2] = var5[2] * var4 / 255;
                int[] var6 = new int[3];
                rgb2lab(var5[0], var5[1], var5[2], var6);
                baseColorsRGB[var2][var3] = var5;
                baseColorsLAB[var2][var3] = var6;
            }
        }

        for (var2 = 0; var2 < 51; ++var2) {
            for (var3 = 0; var3 < 3; ++var3) {
                allowColor[var2][var3] = false;
                if (!force2D || var3 == 1) {
                    allowColor[var2][var3] = true;
                }
            }
        }

        if (force2D) {
            allowColor[11][0] = false;
            allowColor[11][1] = false;
            allowColor[11][2] = true;
        }

        boolean[] var7 = new boolean[3];
        if (grayscale) {
            for (var3 = 0; var3 < 51; ++var3) {
                boolean var9 = false;

                for (int var8 = 0; var8 < var1.length; ++var8) {
                    if (var1[var8] == var3) {
                        var9 = true;
                    }
                }

                if (!var9) {
                    allowColor[var3] = var7;
                }
            }
        }

        if (!allowLapis) {
            allowColor[31] = var7;
        }

        if (!allowEmerald) {
            allowColor[32] = var7;
        }

        if (!allowTNT) {
            allowColor[3] = var7;
        }

        if (!allowBrewingStand) {
            allowColor[5] = var7;
        }

        if (!allowGoldPlates) {
            allowColor[29] = var7;
        }

        if (!allowWater) {
            allowColor[11] = var7;
        }

        if (!allowBed && !allowCobweb) {
            allowColor[2] = var7;
        }

        if (forceDiorite) {
            bestBlockId[13] = 1;
            bestBlockData[13] = 3;
        }

    }

    private HashMap<String, Integer> countSpecialBlocks(int[][][][] var0) {
        HashMap var1 = new HashMap();
        String[] var2 = new String[]{"cobwebs in direct contact with water", "Lapis blocks", "Emerald blocks", "Cobwebs", "TNT", "Glass blocks", "Ice blocks", "Brewing Stands", "Beds", "Light (Golden) Pressure Plates", "Diorite"};
        int[] var3 = new int[var2.length];
        int var4 = var0[0][0].length;

        int var5;
        for (var5 = 1; var5 < 129; ++var5) {
            for (int var6 = 1; var6 < var4; ++var6) {
                for (int var7 = 1; var7 < 129; ++var7) {
                    int var10002;
                    switch (var0[0][var5][var6][var7]) {
                        case 1:
                            if (var0[1][var5][var6][var7] == 3) {
                                var10002 = var3[10]++;
                            }
                            break;
                        case 20:
                            var10002 = var3[5]++;
                            break;
                        case 22:
                            var10002 = var3[1]++;
                            break;
                        case 26:
                            var10002 = var3[8]++;
                            break;
                        case 30:
                            var10002 = var3[3]++;
                            if (var0[0][var5 + 1][var6][var7] == 8 || var0[0][var5 - 1][var6][var7] == 8 || var0[0][var5][var6][var7 + 1] == 8 || var0[0][var5][var6][var7 - 1] == 8) {
                                var10002 = var3[0]++;
                            }
                            break;
                        case 46:
                            var10002 = var3[4]++;
                            break;
                        case 79:
                            var10002 = var3[6]++;
                            break;
                        case 117:
                            var10002 = var3[7]++;
                            break;
                        case 133:
                            var10002 = var3[2]++;
                            break;
                        case 147:
                            var10002 = var3[9]++;
                    }
                }
            }
        }

        var3[8] /= 2;

        for (var5 = 0; var5 < var2.length; ++var5) {
            var1.put(var2[var5], var3[var5]);
        }

        return var1;
    }

    private void saveColorPaletteImage(String var0) {
        byte var1 = 4;
        short var2 = 128;
        int var3 = 0;

        int var4;
        int var5;
        for (var4 = 0; var4 < allowColor.length; ++var4) {
            for (var5 = 0; var5 < allowColor[var4].length; ++var5) {
                if (allowColor[var4][var5]) {
                    ++var3;
                }
            }
        }

        var4 = var3 * var1;
        var5 = 0;
        BufferedImage var6 = new BufferedImage(var2, var4, 1);

        for (int var7 = 0; var7 < 51; ++var7) {
            for (int var8 = 0; var8 < allowColor[var7].length; ++var8) {
                if (allowColor[var7][var8]) {
                    for (int var9 = 0; var9 < var1; ++var9) {
                        for (int var10 = 0; var10 < var2; ++var10) {
                            int var11 = getRGBint(baseColorsRGB[var7][var8][0], baseColorsRGB[var7][var8][1], baseColorsRGB[var7][var8][2]);
                            var6.setRGB(var10, var1 * var5 + var9, var11);
                        }
                    }

                    ++var5;
                }
            }
        }

        saveImage(var6, var0);
    }

    private int[][][][] createTerrainFromNormalizedData(int[][] var0, int[][] var1, BufferedImage var2, BufferedImage var3, BufferedImage var4, int var5, int var6) {
        int[][][][] var7 = new int[2][130][277][130];
        int var8 = 1;
        ArrayList var9 = new ArrayList();
        ArrayList var10 = new ArrayList();

        int var11;
        int var12;
        int var13;
        int var14;
        int var19;
        int var20;
        int var21;
        int var30;
        int var40;
        for (var11 = 0; var11 < 128; ++var11) {
            var12 = 139;
            var13 = 139;
            var14 = 0;
            boolean var15 = true;
            ArrayList var16 = new ArrayList();
            ArrayList var17 = new ArrayList();
            boolean var18 = false;

            for (var19 = 127; var19 >= 0; --var19) {
                var30 = 139 + var14;
                var20 = var1[var11][var19];
                var21 = var0[var11][var19];
                var14 += var21;
                var12 = Math.min(var30, var12);
                var13 = Math.max(var30, var13);
                var7[0][var11 + 1][var30][var19 + 1] = bestBlockId[var20];
                var7[1][var11 + 1][var30][var19 + 1] = bestBlockData[var20];
                if (bestBlockNeedsSupport[var20]) {
                    switch (bestBlockId[var20]) {
                        case 8:
                            byte var22 = 1;
                            if (var21 == 0) {
                                var22 = 5;
                            }

                            if (var21 == 1) {
                                var22 = 10;
                            }

                            for (var40 = 0; var40 < var22; ++var40) {
                                var7[0][var11 + 1][var30 - var40][var19 + 1] = 8;
                                int[] var24 = new int[]{var11 + 1, var30 - var40, var19 + 1};
                                var17.add(var24);
                            }

                            var12 = Math.min(var30 - var22, var12);
                            var7[0][var11 + 1][var30 - var22][var19 + 1] = 20;
                            var18 = true;
                            var14 -= var21;
                            break;
                        case 26:
                            var7[0][var11 + 1][var30][var19 + 1] = 7;
                            var7[0][var11 + 1][var30 - 1][var19 + 1] = supportBlockId;
                            var7[1][var11 + 1][var30 - 1][var19 + 1] = supportBlockData;
                            var12 = Math.min(var30 - 1, var12);
                            var18 = true;
                            int[] var23 = new int[]{var11 + 1, var30, var19 + 1};
                            var16.add(var23);
                            break;
                        default:
                            var7[0][var11 + 1][var30 - 1][var19 + 1] = supportBlockId;
                            var7[1][var11 + 1][var30 - 1][var19 + 1] = supportBlockData;
                            var12 = Math.min(var30 - 1, var12);
                            var18 = true;
                    }
                }
            }

            var30 = 139 + var14;
            var12 = Math.min(var30, var12);
            var13 = Math.max(var30, var13);
            var7[0][var11 + 1][var30][0] = supportBlockId;
            var7[1][var11 + 1][var30][0] = supportBlockData;
            byte var37 = 0;
            if (!var18 && force2D) {
                var37 = 1;
            }

            for (var20 = 0; var20 < 130; ++var20) {
                for (var21 = var12; var21 <= var13; ++var21) {
                    if (var7[0][var11 + 1][var21][var20] != 0) {
                        var7[0][var11 + 1][var21 - var12 + var37][var20] = var7[0][var11 + 1][var21][var20];
                        var7[0][var11 + 1][var21][var20] = 0;
                        var7[1][var11 + 1][var21 - var12 + var37][var20] = var7[1][var11 + 1][var21][var20];
                        var7[1][var11 + 1][var21][var20] = 0;
                    }
                }
            }

            int[] var38;
            for (var20 = 0; var20 < var17.size(); ++var20) {
                var38 = (int[]) var17.get(var20);
                var38[1] = var38[1] - var12 + var37;
                var10.add(var38);
            }

            for (var20 = 0; var20 < var16.size(); ++var20) {
                var38 = (int[]) var16.get(var20);
                var38[1] = var38[1] - var12 + var37;
                var9.add(var38);
            }

            var8 = Math.max(var8, var13 - var12 + 1);
        }

        var11 = var9.size();
        int var32;
        int var36;
        int var42;
        int var44;
        if (allowBed) {
            byte var26 = 9;
            byte var27 = 1;
            byte var29 = 8;
            byte var31 = 0;

            int[] var34;
            for (var32 = 0; var32 < var11; ++var32) {
                var34 = (int[]) var9.get(var32);
                var36 = var34[0];
                var19 = var34[1];
                var20 = var34[2];

                for (var21 = var32 + 1; var21 < var11; ++var21) {
                    int[] var39 = (int[]) var9.get(var21);
                    var40 = var39[0];
                    var44 = var39[1];
                    int var25 = var39[2];
                    if (var19 == var44 && (var36 == var40 && Math.abs(var20 - var25) == 1 || var20 == var25 && Math.abs(var36 - var40) == 1)) {
                        var7[0][var36][var19][var20] = 26;
                        var7[0][var40][var44][var25] = 26;
                        if (Math.abs(var20 - var25) == 1) {
                            if (var20 > var25) {
                                var7[1][var36][var19][var20] = var29;
                                var7[1][var40][var44][var25] = var31;
                            } else {
                                var7[1][var36][var19][var20] = var31;
                                var7[1][var40][var44][var25] = var29;
                            }
                        } else if (var36 < var40) {
                            var7[1][var36][var19][var20] = var26;
                            var7[1][var40][var44][var25] = var27;
                        } else {
                            var7[1][var36][var19][var20] = var27;
                            var7[1][var40][var44][var25] = var26;
                        }

                        var9.remove(var21);
                        var9.remove(var32);
                        var21 = var11;
                        --var32;
                        var11 -= 2;
                    }
                }
            }

            boolean var43;
            for (var32 = 0; var32 < var11; ++var32) {
                var34 = (int[]) var9.get(var32);
                var36 = var34[0];
                var19 = var34[1];
                var20 = var34[2];
                var43 = false;
                if (!var43 && var36 == 1) {
                    var7[0][1][var19][var20] = 26;
                    var7[1][1][var19][var20] = var27;
                    var7[0][0][var19][var20] = 26;
                    var7[1][0][var19][var20] = var26;
                    var7[0][0][var19 - 1][var20] = supportBlockId;
                    var7[1][0][var19 - 1][var20] = supportBlockData;
                    var43 = true;
                }

                if (!var43 && var36 == 128) {
                    var7[0][128][var19][var20] = 26;
                    var7[1][128][var19][var20] = var26;
                    var7[0][129][var19][var20] = 26;
                    var7[1][129][var19][var20] = var27;
                    var7[0][129][var19 - 1][var20] = supportBlockId;
                    var7[1][129][var19 - 1][var20] = supportBlockData;
                    var43 = true;
                }

                if (!var43 && var20 == 128) {
                    var7[0][var36][var19][128] = 26;
                    var7[1][var36][var19][128] = var31;
                    var7[0][var36][var19][129] = 26;
                    var7[1][var36][var19][129] = var29;
                    var7[0][var36][var19 - 1][129] = supportBlockId;
                    var7[1][var36][var19 - 1][129] = supportBlockData;
                    var43 = true;
                }

                if (!var43 && var20 == 1 && var7[0][var36][var19 - 1][0] != 1) {
                    var7[0][var36][var19][1] = 26;
                    var7[1][var36][var19][1] = var29;
                    var7[0][var36][var19][0] = 26;
                    var7[1][var36][var19][0] = var31;
                    var7[0][var36][var19 - 1][0] = supportBlockId;
                    var7[1][var36][var19 - 1][0] = supportBlockData;
                    var43 = true;
                }

                if (var43) {
                    var9.remove(var32);
                    --var32;
                    --var11;
                }
            }

            for (var32 = 0; var32 < var11; ++var32) {
                var34 = (int[]) var9.get(var32);
                var36 = var34[0];
                var19 = var34[1];
                var20 = var34[2];
                var43 = false;
                if (!var43 && (var7[0][var36][var19][var20 - 1] == 0 || var7[0][var36][var19][var20 - 1] == 20) && var7[0][var36][var19 + 1][var20 - 1] != 0 && var7[0][var36][var19 + 1][var20 - 1] != 20 && !bestBlockNeedsSupport[var1[var36 - 1][var20 - 1 - 1]]) {
                    var43 = true;
                    var7[0][var36][var19][var20] = 26;
                    var7[1][var36][var19][var20] = var29;
                    var7[0][var36][var19][var20 - 1] = 26;
                    var7[1][var36][var19][var20 - 1] = var31;
                    var7[0][var36][var19 - 1][var20 - 1] = supportBlockId;
                    var7[1][var36][var19 - 1][var20 - 1] = supportBlockData;
                }

                if (!var43 && (var7[0][var36][var19][var20 + 1] == 0 || var7[0][var36][var19][var20 + 1] == 20) && var7[0][var36][var19 + 1][var20 + 1] != 0 && var7[0][var36][var19 + 1][var20 + 1] != 20 && !bestBlockNeedsSupport[var1[var36 - 1][var20 - 1 + 1]]) {
                    var43 = true;
                    var7[0][var36][var19][var20] = 26;
                    var7[1][var36][var19][var20] = var31;
                    var7[0][var36][var19][var20 + 1] = 26;
                    var7[1][var36][var19][var20 + 1] = var29;
                    var7[0][var36][var19 - 1][var20 + 1] = supportBlockId;
                    var7[1][var36][var19 - 1][var20 + 1] = supportBlockData;
                }

                if (var43) {
                    var9.remove(var32);
                    --var32;
                    --var11;
                }
            }

            for (var32 = 0; var32 < var11; ++var32) {
                var34 = (int[]) var9.get(var32);
                var36 = var34[0];
                var19 = var34[1];
                var20 = var34[2];

                for (var21 = 0; var7[0][var36 - 1][var21][var20] == 0 || var7[0][var36 - 1][var21][var20] == 20; ++var21) {
                }

                var42 = var21;

                for (var21 = 0; var7[0][var36 + 1][var21][var20] == 0 || var7[0][var36 + 1][var21][var20] == 20; ++var21) {
                }

                boolean var45 = false;
                if (!var45 && var42 > var19) {
                    var7[0][var36][var19][var20] = 26;
                    var7[1][var36][var19][var20] = var27;
                    var7[0][var36 - 1][var19][var20] = 26;
                    var7[1][var36 - 1][var19][var20] = var26;
                    var7[0][var36 - 1][var19 - 1][var20] = supportBlockId;
                    var7[1][var36 - 1][var19 - 1][var20] = supportBlockData;
                    var45 = true;
                }

                if (!var45 && var21 > var19) {
                    var7[0][var36][var19][var20] = 26;
                    var7[1][var36][var19][var20] = var26;
                    var7[0][var36 + 1][var19][var20] = 26;
                    var7[1][var36 + 1][var19][var20] = var27;
                    var7[0][var36 + 1][var19 - 1][var20] = supportBlockId;
                    var7[1][var36 + 1][var19 - 1][var20] = supportBlockData;
                    var45 = true;
                }

                if (var45) {
                    var9.remove(var32);
                    --var32;
                    --var11;
                }
            }
        }

        int[] var28;
        if (allowCobweb) {
            for (var12 = 0; var12 < var11; ++var12) {
                var28 = (int[]) var9.get(var12);
                var14 = var28[0];
                var30 = var28[1];
                var32 = var28[2];
                var7[0][var14][var30][var32] = 30;
            }
        } else {
            for (var12 = 0; var12 < var11; ++var12) {
                var28 = (int[]) var9.get(var12);
                var14 = var28[0] - 1;
                var30 = var28[2] - 1;
                var32 = var28[0];
                int var35 = var28[1];
                var36 = var28[2];
                var19 = 1 - var0[var14][var30];
                boolean[][] var41 = new boolean[51][3];

                for (var21 = 0; var21 < 51; ++var21) {
                    for (var42 = 0; var42 < 3; ++var42) {
                        var41[var21][var42] = allowColor[var21][var42];
                        if (var42 != var19 || var21 == 2) {
                            var41[var21][var42] = false;
                        }
                    }
                }

                var21 = findClosestBaseColor(var2.getRGB(var14, var30), var41)[0];
                var42 = bestBlockId[var21];
                var40 = bestBlockData[var21];
                var7[0][var32][var35][var36] = var42;
                var7[1][var32][var35][var36] = var40;
                if (!bestBlockNeedsSupport[var21]) {
                    var7[0][var32][var35 - 1][var36] = 0;
                    var7[1][var32][var35 - 1][var36] = 0;
                }

                var44 = getRGBint(baseColorsRGB[var21][var19][0], baseColorsRGB[var21][var19][1], baseColorsRGB[var21][var19][2]);
                var3.setRGB(var14, var30, var44);
                var4.setRGB(var5 * 128 + var14, var6 * 128 + var30, var44);
            }

            unplacedBeds += var11;
        }

        for (var12 = 0; var12 < var10.size(); ++var12) {
            var28 = (int[]) var10.get(var12);
            var14 = var28[0];
            var30 = var28[1];
            var32 = var28[2];
            if (var7[0][var14 - 1][var30][var32] == 0) {
                var7[0][var14 - 1][var30][var32] = 20;
            }

            if (var7[0][var14 + 1][var30][var32] == 0) {
                var7[0][var14 + 1][var30][var32] = 20;
            }

            if (var7[0][var14][var30][var32 + 1] == 0) {
                var7[0][var14][var30][var32 + 1] = 20;
            }

            if (var7[0][var14][var30][var32 - 1] == 0) {
                var7[0][var14][var30][var32 - 1] = 20;
            }
        }

        if (force2D && var8 == 1) {
            for (var12 = 0; var12 < 130; ++var12) {
                for (var13 = 0; var13 < 130; ++var13) {
                    var7[0][var12][0][var13] = var7[0][var12][1][var13];
                    var7[1][var12][0][var13] = var7[1][var12][1][var13];
                }
            }
        }

        int[][][][] var33 = new int[2][130][var8 + 1][130];

        for (var13 = 0; var13 < 2; ++var13) {
            for (var14 = 0; var14 < 130; ++var14) {
                for (var30 = 0; var30 < 130; ++var30) {
                    if (var14 != 0 && var14 != 129 && var30 != 0 && var30 != 129) {
                        var33[0][var14][0][var30] = 20;
                    }

                    for (var32 = 0; var32 < var8; ++var32) {
                        var33[var13][var14][var32 + 1][var30] = var7[var13][var14][var32][var30];
                    }
                }
            }
        }

        return var33;
    }

    private void saveSchematicFromTerrain(int[][][][] var0, String var1) {
        int var2 = var0[0].length;
        int var3 = var0[0][0].length;
        int var4 = var0[0][0][0].length;
        byte[] var5 = new byte[var2 * var3 * var4];
        byte[] var6 = new byte[var2 * var3 * var4];

        for (int var7 = 0; var7 < var2; ++var7) {
            for (int var8 = 0; var8 < var3; ++var8) {
                for (int var9 = 0; var9 < var4; ++var9) {
                    int var10 = (var8 * var4 + var9) * var2 + var7;
                    var5[var10] = (byte) var0[0][var7][var8][var9];
                    var6[var10] = (byte) var0[1][var7][var8][var9];
                }
            }
        }

        ByteArrayTag var22 = new ByteArrayTag("Blocks", var5);
        ByteArrayTag var23 = new ByteArrayTag("Data", var6);
        StringTag var24 = new StringTag("Materials", "Alpha");
        ShortTag var25 = new ShortTag("Width", (short) var2);
        ShortTag var11 = new ShortTag("Height", (short) var3);
        ShortTag var12 = new ShortTag("Length", (short) var4);
        ArrayList var13 = new ArrayList();
        ListTag var14 = new ListTag("Entities", CompoundTag.class, var13);
        ArrayList var15 = new ArrayList();
        ListTag var16 = new ListTag("TileEntities", CompoundTag.class, var15);
        HashMap var17 = new HashMap();
        var17.put("Width", var25);
        var17.put("Height", var11);
        var17.put("Length", var12);
        var17.put("Materials", var24);
        var17.put("Blocks", var22);
        var17.put("Data", var23);
        var17.put("Entities", var14);
        var17.put("TileEntities", var16);
        CompoundTag var18 = new CompoundTag("Schematic", var17);

        try {
            FileOutputStream var19 = new FileOutputStream(new File(var1));
            NBTOutputStream var20 = new NBTOutputStream(var19);
            var20.writeTag(var18);
            var20.close();
        } catch (Exception var21) {
            System.out.println("Something went wrong:");
            var21.printStackTrace();
        }

    }

    private int[][] getMapColorMatrix(BufferedImage var0, BufferedImage var1, boolean[][] var2, boolean var3) {
        int var4 = var0.getWidth();
        int var5 = var0.getHeight();
        int[][] var6 = new int[var4][var5];
        int var7 = var4 * var5;
        int var8 = 0;
        int var9 = 0;
        HashMap var10 = new HashMap();
        int[][] var11 = new int[][]{{1, 0, 7}, {-1, 1, 3}, {0, 1, 5}, {1, 1, 1}};
        byte var12 = 16;

        for (int var13 = 0; var13 < var5; ++var13) {
            for (int var14 = 0; var14 < var4; ++var14) {
                int[] var10000 = new int[]{0, 0};
                int var16 = var0.getRGB(var14, var13);
                int[] var17 = getRGBtriple(var16);
                int[] var15;
                if (var10.containsKey(var16)) {
                    var15 = (int[]) var10.get(var16);
                } else {
                    var15 = findClosestBaseColor(var16, var2);
                    var10.put(var16, var15);
                }

                int var18 = getRGBint(baseColorsRGB[var15[0]][var15[1]]);
                var0.setRGB(var14, var13, var18);
                var6[var14][var13] = 3 * var15[0] + var15[1];
                if (dithering) {
                    int[] var19 = getRGBtriple(var18);
                    int[] var20 = new int[3];

                    int var21;
                    for (var21 = 0; var21 < 3; ++var21) {
                        var20[var21] = var17[var21] - var19[var21];
                    }

                    var21 = 0;
                    int var22 = 0;
                    int[] var23 = new int[]{0, 0, 0};
                    int var24 = var11.length - 1;

                    while (true) {
                        if (var24 < 0) {
                            if (var1 != null && var14 + var21 < var4 && var14 + var21 >= 0 && var13 + var22 < var5 && var13 + var22 >= 0) {
                                var1.setRGB(var14 + var21, var13 + var22, getRGBint(var23));
                            }
                            break;
                        }

                        var21 = var11[var24][0];
                        var22 = var11[var24][1];
                        if (var14 + var21 < var4 && var14 + var21 >= 0 && var13 + var22 < var5 && var13 + var22 >= 0) {
                            var23 = getRGBtriple(var0.getRGB(var14 + var21, var13 + var22));

                            for (int var25 = 0; var25 < 3; ++var25) {
                                var23[var25] = (int) Math.min(Math.max((double) var23[var25] + (double) var20[var25] * (double) var11[var24][2] / (double) var12, 0.0D), 255.0D);
                            }

                            var0.setRGB(var14 + var21, var13 + var22, getRGBint(var23));
                        }

                        --var24;
                    }
                }

                if (var3) {
                    ++var8;
                    int var26 = var8 * 100 / var7;
                    if (var26 % 5 == 0 && var26 != var9) {
                        if (var26 == 5) {
                            System.out.print("  ");
                        }

                        System.out.print(var26 + "% ");
                        var9 = var26;
                        if (var26 == 100) {
                            System.out.println();
                        }
                    }
                }
            }
        }

        return var6;
    }

    private int[] findClosestBaseColor(int var0, boolean[][] var1) {
        double var2 = 1.7976931348623157E308D;
        int[] var4 = new int[]{0, 0};
        int[] var5 = getRGBtriple(var0);

        for (int var6 = 0; var6 < 51; ++var6) {
            for (int var7 = 0; var7 < baseColorsRGB[var6].length; ++var7) {
                if (var1[var6][var7]) {
                    double var8 = colorDifferenceRGBLAB(var5, baseColorsLAB[var6][var7]);
                    if (var8 < var2) {
                        var4[0] = var6;
                        var4[1] = var7;
                        var2 = var8;
                    }
                }
            }
        }

        return var4;
    }

    private int getRGBint(int[] var0) {
        return getRGBint(var0[0], var0[1], var0[2]);
    }

    private int getRGBint(int var0, int var1, int var2) {
        return 65536 * var0 + 256 * var1 + var2;
    }

    private int[] getRGBtriple(int var0) {
        int[] var1 = new int[]{var0 >> 16 & 255, var0 >> 8 & 255, var0 & 255};
        return var1;
    }

    private BufferedImage loadImage(String var0) {
        BufferedImage var1 = null;
        File var2 = new File(var0);

        try {
            var1 = ImageIO.read(var2);
        } catch (IOException var4) {
            System.out.println("Something went wrong:");
            var4.printStackTrace();
        }

        return var1;
    }

    private void saveImage(BufferedImage var0, String var1) {
        File var2 = new File(var1);

        try {
            ImageIO.write(var0, "png", var2);
        } catch (IOException var4) {
            System.out.println("Something went wrong:");
            var4.printStackTrace();
        }

    }

    private double colorDifferenceRGBLAB(int[] var0, int[] var1) {
        int[] var2 = new int[3];
        rgb2lab(var0[0], var0[1], var0[2], var2);
        return euclideanDistanceSquared(var2[0], var2[1], var2[2], var1[0], var1[1], var1[2]);
    }

    private double euclideanDistanceSquared(int var0, int var1, int var2, int var3, int var4, int var5) {
        return (double) ((var0 - var3) * (var0 - var3) + (var1 - var4) * (var1 - var4) + (var2 - var5) * (var2 - var5));
    }

    private void rgb2lab(int var0, int var1, int var2, int[] var3) {
        float var19 = 0.008856452F;
        float var20 = 903.2963F;
        float var21 = 0.964221F;
        float var22 = 1.0F;
        float var23 = 0.825211F;
        float var4 = (float) var0 / 255.0F;
        float var5 = (float) var1 / 255.0F;
        float var6 = (float) var2 / 255.0F;
        if ((double) var4 <= 0.04045D) {
            var4 /= 12.0F;
        } else {
            var4 = (float) Math.pow(((double) var4 + 0.055D) / 1.055D, 2.4D);
        }

        if ((double) var5 <= 0.04045D) {
            var5 /= 12.0F;
        } else {
            var5 = (float) Math.pow(((double) var5 + 0.055D) / 1.055D, 2.4D);
        }

        if ((double) var6 <= 0.04045D) {
            var6 /= 12.0F;
        } else {
            var6 = (float) Math.pow(((double) var6 + 0.055D) / 1.055D, 2.4D);
        }

        float var7 = 0.43605202F * var4 + 0.3850816F * var5 + 0.14308742F * var6;
        float var8 = 0.22249159F * var4 + 0.71688604F * var5 + 0.060621485F * var6;
        float var9 = 0.013929122F * var4 + 0.097097F * var5 + 0.7141855F * var6;
        float var13 = var7 / var21;
        float var14 = var8 / var22;
        float var15 = var9 / var23;
        float var10;
        if (var13 > var19) {
            var10 = (float) Math.pow((double) var13, 0.3333333333333333D);
        } else {
            var10 = (float) (((double) (var20 * var13) + 16.0D) / 116.0D);
        }

        float var11;
        if (var14 > var19) {
            var11 = (float) Math.pow((double) var14, 0.3333333333333333D);
        } else {
            var11 = (float) (((double) (var20 * var14) + 16.0D) / 116.0D);
        }

        float var12;
        if (var15 > var19) {
            var12 = (float) Math.pow((double) var15, 0.3333333333333333D);
        } else {
            var12 = (float) (((double) (var20 * var15) + 16.0D) / 116.0D);
        }

        float var16 = 116.0F * var11 - 16.0F;
        float var17 = 500.0F * (var10 - var11);
        float var18 = 200.0F * (var11 - var12);
        var3[0] = (int) (2.55D * (double) var16 + 0.5D);
        var3[1] = (int) ((double) var17 + 0.5D);
        var3[2] = (int) ((double) var18 + 0.5D);
    }
}
