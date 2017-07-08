package org.dynmap.hdmap.renderer;

import java.util.ArrayList;
import java.util.Map;

import org.dynmap.renderer.CustomRenderer;
import org.dynmap.renderer.MapDataContext;
import org.dynmap.renderer.RenderPatch;
import org.dynmap.renderer.RenderPatchFactory;

/**
 * Simple renderer for handling single and double chests
 */
public class ChestRenderer extends CustomRenderer {
    private enum ChestData {
        SINGLE_WEST, SINGLE_SOUTH, SINGLE_EAST, SINGLE_NORTH, LEFT_WEST, LEFT_SOUTH, LEFT_EAST, LEFT_NORTH, RIGHT_WEST, RIGHT_SOUTH, RIGHT_EAST, RIGHT_NORTH
    };
    // Models, indexed by ChestData.ordinal()
    private RenderPatch[][] models = new RenderPatch[ChestData.values().length][];

    private static final double OFF1 = 1.0 / 16.0;
    private static final double OFF14 = 14.0 / 16.0;
    private static final double OFF15 = 15.0 / 16.0;
    
    private static final int[] SINGLE_PATCHES = { 5, 0, 1, 2, 4, 3 };
    private static final int[] LEFT_PATCHES = { 14, 6, 10, 11, 12, 8 };
    private static final int[] RIGHT_PATCHES = { 15, 7, 10, 11, 13, 9 };

    @Override
    public boolean initializeRenderer(RenderPatchFactory rpf, int blkid, int blockdatamask, Map<String,String> custparm) {
        if(!super.initializeRenderer(rpf, blkid, blockdatamask, custparm))
            return false;

        ArrayList<RenderPatch> list = new ArrayList<RenderPatch>();
        // Build single chest patch model 
        CustomRenderer.addBox(rpf, list, OFF1, OFF15, 0, OFF14, OFF1, OFF15, SINGLE_PATCHES);
        models[ChestData.SINGLE_SOUTH.ordinal()] = list.toArray(new RenderPatch[list.size()]);
        // Rotate to other orientations
        models[ChestData.SINGLE_EAST.ordinal()] = new RenderPatch[6];
        models[ChestData.SINGLE_NORTH.ordinal()] = new RenderPatch[6];
        models[ChestData.SINGLE_WEST.ordinal()] = new RenderPatch[6];
        for (int i = 0; i < 6; i++) {
            models[ChestData.SINGLE_WEST.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 90, 0, SINGLE_PATCHES[i]); 
            models[ChestData.SINGLE_NORTH.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 180, 0, SINGLE_PATCHES[i]); 
            models[ChestData.SINGLE_EAST.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 270, 0, SINGLE_PATCHES[i]); 
        }
        // Build left half model for double chest
        list.clear();
        CustomRenderer.addBox(rpf, list, OFF1, 1, 0, OFF14, OFF1, OFF15, LEFT_PATCHES);
        models[ChestData.LEFT_SOUTH.ordinal()] = list.toArray(new RenderPatch[list.size()]);
        // Rotate to other orientations
        models[ChestData.LEFT_EAST.ordinal()] = new RenderPatch[6];
        models[ChestData.LEFT_NORTH.ordinal()] = new RenderPatch[6];
        models[ChestData.LEFT_WEST.ordinal()] = new RenderPatch[6];
        for (int i = 0; i < 6; i++) {
            models[ChestData.LEFT_WEST.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 90, 0, LEFT_PATCHES[i]); 
            models[ChestData.LEFT_NORTH.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 180, 0, LEFT_PATCHES[i]); 
            models[ChestData.LEFT_EAST.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 270, 0, LEFT_PATCHES[i]); 
        }
        // Build right half model for double chest
        list.clear();
        CustomRenderer.addBox(rpf, list, 0, OFF15, 0, OFF14, OFF1, OFF15, RIGHT_PATCHES);
        models[ChestData.RIGHT_SOUTH.ordinal()] = list.toArray(new RenderPatch[list.size()]);
        // Rotate to other orientations
        models[ChestData.RIGHT_EAST.ordinal()] = new RenderPatch[6];
        models[ChestData.RIGHT_NORTH.ordinal()] = new RenderPatch[6];
        models[ChestData.RIGHT_WEST.ordinal()] = new RenderPatch[6];
        for (int i = 0; i < 6; i++) {
            models[ChestData.RIGHT_WEST.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 90, 0, RIGHT_PATCHES[i]); 
            models[ChestData.RIGHT_NORTH.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 180, 0, RIGHT_PATCHES[i]); 
            models[ChestData.RIGHT_EAST.ordinal()][i] = rpf.getRotatedPatch(list.get(i), 0, 270, 0, RIGHT_PATCHES[i]); 
        }
        
        return true;
    }

    @Override
    public int getMaximumTextureCount() {
        return 16;
    }
        
    @Override
    public RenderPatch[] getRenderPatchList(MapDataContext ctx) {
        int blktype = ctx.getBlockTypeID();
        int blkdata = ctx.getBlockData();   /* Get block data */
        ChestData cd = ChestData.SINGLE_NORTH;   /* Default to single facing north */
        switch(blkdata) {   /* First, use orientation data */
            case 2: /* North */
                if(ctx.getBlockTypeIDAt(-1, 0, 0) == blktype) {
                    cd = ChestData.LEFT_NORTH;
                }
                else if(ctx.getBlockTypeIDAt(1, 0, 0) == blktype) {
                    cd = ChestData.RIGHT_NORTH;
                }
                else {
                    cd = ChestData.SINGLE_NORTH;
                }
                break;
            case 4: /* West */
                if(ctx.getBlockTypeIDAt(0, 0, -1) == blktype) {
                    cd = ChestData.RIGHT_WEST;
                }
                else if(ctx.getBlockTypeIDAt(0, 0, 1) == blktype) {
                    cd = ChestData.LEFT_WEST;
                }
                else {
                    cd = ChestData.SINGLE_WEST;
                }
                break;
            case 5: /* East */
                if(ctx.getBlockTypeIDAt(0, 0, -1) == blktype) {
                    cd = ChestData.LEFT_EAST;
                }
                else if(ctx.getBlockTypeIDAt(0, 0, 1) == blktype) {
                    cd = ChestData.RIGHT_EAST;
                }
                else {
                    cd = ChestData.SINGLE_EAST;
                }
                break;
            case 3: /* South */
            default:
                if(ctx.getBlockTypeIDAt(-1, 0, 0) == blktype) {
                    cd = ChestData.RIGHT_SOUTH;
                }
                else if(ctx.getBlockTypeIDAt(1, 0, 0) == blktype) {
                    cd = ChestData.LEFT_SOUTH;
                }
                else {
                    cd = ChestData.SINGLE_SOUTH;
                }
                break;
        }
        return models[cd.ordinal()];
    }
}
