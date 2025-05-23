package com.greatorator.tolkienmobs.world.components.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TolkienSurfaceRuleManager
{
    private static Map<RuleCategory, Map<String, SurfaceRules.RuleSource>> surfaceRules = Maps.newHashMap();
    private static Map<RuleCategory, SurfaceRules.RuleSource> defaultSurfaceRules = Maps.newHashMap();
    private static Map<RuleCategory, Map<RuleStage, List<Pair<Integer, SurfaceRules.RuleSource>>>> defaultSurfaceRuleInjections = Maps.newHashMap();

    /**
     * Add surface rules for biomes belonging to a modded namespace.
     * @param category the category to add surface rules for.
     * @param namespace the namespace to use these rules for.
     * @param rules the rules to add.
     */
    public static void addSurfaceRules(RuleCategory category, String namespace, SurfaceRules.RuleSource rules)
    {
        surfaceRules.get(category).put(namespace, rules);
    }

    /**
     * Add surface rules to be inserted into the default surface rules at a given stage.
     * @param category the category of the surface rules.
     * @param ruleStage the stage to add the surface rules to.
     * @param priority the priority of the surface rules.
     * @param rules the rules to add.
     */
    public static void addToDefaultSurfaceRulesAtStage(RuleCategory category, RuleStage ruleStage, int priority, SurfaceRules.RuleSource rules)
    {
        defaultSurfaceRuleInjections.get(category).get(ruleStage).add(Pair.of(priority, rules));
    }

    /**
     * Set the default surface rules for a category.
     * @param category the category of the surface rules.
     * @param rules the new default surface rules.
     */
    public static void setDefaultSurfaceRules(RuleCategory category, SurfaceRules.RuleSource rules)
    {
        defaultSurfaceRules.put(category, rules);
    }

    /**
     * Remove surface rules for biomes belonging to a modded namespace.
     * @param category the category to add surface rules for.
     * @param namespace the namespace to use these rules for.
     */
    public static void removeSurfaceRules(RuleCategory category, String namespace)
    {
        surfaceRules.get(category).remove(namespace);
    }

    /**
     * Gets the namespaced rules for a given category.
     * @param category the category to get the surface rules for.
     * @param fallback the surface rules to fallback on.
     * @return the namespaced rules.
     */
    public static SurfaceRules.RuleSource getNamespacedRules(RuleCategory category, SurfaceRules.RuleSource fallback)
    {
        ImmutableMap.Builder<String, SurfaceRules.RuleSource> builder = ImmutableMap.builder();
        builder.put("minecraft", getDefaultSurfaceRules(category));
        builder.putAll(surfaceRules.get(category));
        return new NamespacedSurfaceRuleSource(fallback, builder.build());
    }

    /**
     * Get the surface rules to be added to a given stage.
     * @param category the category of the surface rules.
     * @param ruleStage the stage of the surface rules.
     * @return list of the surface rules to be added.
     */
    public static List<SurfaceRules.RuleSource> getDefaultSurfaceRuleAdditionsForStage(RuleCategory category, RuleStage ruleStage)
    {
        return defaultSurfaceRuleInjections.get(category).get(ruleStage).stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(Pair::getSecond).collect(ImmutableList.toImmutableList());
    }

    /**
     * Get the default surface rules for a category.
     * @param category the category to get the surface rules for.
     * @return the default surface rules.
     */
    public static SurfaceRules.RuleSource getDefaultSurfaceRules(RuleCategory category)
    {
        if (defaultSurfaceRules.containsKey(category))
            return defaultSurfaceRules.get(category);

        if (category == RuleCategory.NETHER)
            return TolkienSurfaceRuleData.nether();
        else if (category == RuleCategory.END) {
            return TolkienSurfaceRuleData.end();
        }

        return TolkienSurfaceRuleData.overworld();
    }

    /**
     * Categories for surface rules to be classed under.
     */
    public enum RuleCategory
    {
        OVERWORLD, NETHER, END
    }

    /**
     * The stage for surface rules to be added to.
     */
    public enum RuleStage
    {
        BEFORE_BEDROCK, AFTER_BEDROCK
    }

    static
    {
        for (RuleCategory category : RuleCategory.values())
        {
            // Initialize the surface rules map
            surfaceRules.put(category, Maps.newHashMap());

            // Initialize the default surface rule injections map
            Map<RuleStage, List<Pair<Integer, SurfaceRules.RuleSource>>> ruleStages = Maps.newHashMap();
            for (RuleStage stage : RuleStage.values())
                ruleStages.put(stage, Lists.newArrayList());
            defaultSurfaceRuleInjections.put(category, ruleStages);
        }
    }
}
