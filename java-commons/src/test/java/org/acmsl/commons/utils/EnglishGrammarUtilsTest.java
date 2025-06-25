//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: EnglishGrammarUtilsTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Tests for EnglishGrammarUtils.
 */
package org.acmsl.commons.utils;

/*
 * Importing Jetbrains annotations.
 */

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for EnglishGrammarUtils.
 * @see org.acmsl.commons.utils.EnglishGrammarUtils
 */
@RunWith(JUnit4.class)
public class EnglishGrammarUtilsTest
{
    /**
     * Tests {EnglishGrammarUtils#getRegularSingularForm(String)}.
     * @see EnglishGrammarUtils#getRegularSingularForm(String)
     */
    @Test
    public void getRegularSingularForm_works()
    {
        final EnglishGrammarUtils t_EnglishGrammarUtils = EnglishGrammarUtils.getInstance();

        Assert.assertEquals(
            "Invalid singular for cars",
            "car",
            t_EnglishGrammarUtils.getRegularSingularForm("cars"));
        Assert.assertEquals(
            "Invalid singular for fast_cars",
            "fast_car",
            t_EnglishGrammarUtils.getRegularSingularForm("fast_cars"));
        Assert.assertEquals(
            "Invalid singular for trees",
            "tree",
            t_EnglishGrammarUtils.getRegularSingularForm("trees"));
        Assert.assertEquals(
            "Invalid singular for tall_trees",
            "tall_tree",
            t_EnglishGrammarUtils.getRegularSingularForm("tall_trees"));
        Assert.assertEquals(
            "Invalid singular for monkeys",
            "monkey",
            t_EnglishGrammarUtils.getRegularSingularForm("monkeys"));
        Assert.assertEquals(
            "Invalid singular for old_monkeys",
            "old_monkey",
            t_EnglishGrammarUtils.getRegularSingularForm("old_monkeys"));
        Assert.assertEquals(
            "Invalid singular for addresses",
            "address",
            t_EnglishGrammarUtils.getRegularSingularForm("addresses"));
        Assert.assertEquals(
            "Invalid singular for postal_addresses",
            "postal_address",
            t_EnglishGrammarUtils.getRegularSingularForm("postal_addresses"));
        Assert.assertEquals(
            "Invalid singular for categories",
            "category",
            t_EnglishGrammarUtils.getRegularSingularForm("categories"));
        Assert.assertEquals(
            "Invalid singular for food_categories",
            "food_category",
            t_EnglishGrammarUtils.getRegularSingularForm("food_categories"));
    }

    /**
     * Tests {EnglishGrammarUtils#getRegularPluralForm(String)}.
     * @see EnglishGrammarUtils#getRegularPluralForm(String)
     */
    @Test
    public void getRegularPluralForm_works()
    {
        final EnglishGrammarUtils t_EnglishGrammarUtils = EnglishGrammarUtils.getInstance();

        Assert.assertEquals(
            "Invalid plural for car",
            "cars",
            t_EnglishGrammarUtils.getRegularPluralForm("car"));
        Assert.assertEquals(
            "Invalid plural for fast_car",
            "fast_cars",
            t_EnglishGrammarUtils.getRegularPluralForm("fast_car"));
        Assert.assertEquals(
            "Invalid plural for tree",
            "trees",
            t_EnglishGrammarUtils.getRegularPluralForm("tree"));
        Assert.assertEquals(
            "Invalid plural for tall_tree",
            "tall_trees",
            t_EnglishGrammarUtils.getRegularPluralForm("tall_tree"));
        Assert.assertEquals(
            "Invalid plural for monkey",
            "monkeys",
            t_EnglishGrammarUtils.getRegularPluralForm("monkey"));
        Assert.assertEquals(
            "Invalid plural for old_monkey",
            "old_monkeys",
            t_EnglishGrammarUtils.getRegularPluralForm("old_monkey"));
        Assert.assertEquals(
            "Invalid plural for category",
            "categories",
            t_EnglishGrammarUtils.getRegularPluralForm("category"));
        Assert.assertEquals(
            "Invalid plural for food_category",
            "food_categories",
            t_EnglishGrammarUtils.getRegularPluralForm("food_category"));
    }

    /**
     * Tests {EnglishGrammarUtils#getSingular(String)}.
     * @see EnglishGrammarUtils#getSingular(String)
     */
    @Test
    public void getSingular_works()
    {
        final EnglishGrammarUtils t_EnglishGrammarUtils = EnglishGrammarUtils.getInstance();

        Assert.assertEquals(
            "Invalid singular for cars",
            "car",
            t_EnglishGrammarUtils.getSingular("cars"));
        Assert.assertEquals(
            "Invalid singular for fast_cars",
            "fast_car",
            t_EnglishGrammarUtils.getSingular("fast_cars"));
        Assert.assertEquals(
            "Invalid singular for trees",
            "tree",
            t_EnglishGrammarUtils.getSingular("trees"));
        Assert.assertEquals(
            "Invalid singular for tall_trees",
            "tall_tree",
            t_EnglishGrammarUtils.getSingular("tall_trees"));
        Assert.assertEquals(
            "Invalid singular for monkeys",
            "monkey",
            t_EnglishGrammarUtils.getSingular("monkeys"));
        Assert.assertEquals(
            "Invalid singular for old_monkeys",
            "old_monkey",
            t_EnglishGrammarUtils.getSingular("old_monkeys"));
        Assert.assertEquals(
            "Invalid singular for addresses",
            "address",
            t_EnglishGrammarUtils.getSingular("addresses"));
        Assert.assertEquals(
            "Invalid singular for postal_addresses",
            "postal_address",
            t_EnglishGrammarUtils.getSingular("postal_addresses"));
        Assert.assertEquals(
            "Invalid singular for categories",
            "category",
            t_EnglishGrammarUtils.getSingular("categories"));
        Assert.assertEquals(
            "Invalid singular for food_categories",
            "food_category",
            t_EnglishGrammarUtils.getSingular("food_categories"));
    }

    /**
     * Tests {EnglishGrammarUtils#getPlural(String)}.
     * @see EnglishGrammarUtils#getPlural(String)
     */
    @Test
    public void getPlural_works()
    {
        final EnglishGrammarUtils t_EnglishGrammarUtils = EnglishGrammarUtils.getInstance();

        Assert.assertEquals(
            "Invalid plural for car",
            "cars",
            t_EnglishGrammarUtils.getPlural("car"));
        Assert.assertEquals(
            "Invalid plural for fast_car",
            "fast_cars",
            t_EnglishGrammarUtils.getPlural("fast_car"));
        Assert.assertEquals(
            "Invalid plural for tree",
            "trees",
            t_EnglishGrammarUtils.getPlural("tree"));
        Assert.assertEquals(
            "Invalid plural for tall_tree",
            "tall_trees",
            t_EnglishGrammarUtils.getPlural("tall_tree"));
        Assert.assertEquals(
            "Invalid plural for monkey",
            "monkeys",
            t_EnglishGrammarUtils.getPlural("monkey"));
        Assert.assertEquals(
            "Invalid plural for old_monkey",
            "old_monkeys",
            t_EnglishGrammarUtils.getPlural("old_monkey"));
        Assert.assertEquals(
            "Invalid plural for category",
            "categories",
            t_EnglishGrammarUtils.getPlural("category"));
        Assert.assertEquals(
            "Invalid plural for food_category",
            "food_categories",
            t_EnglishGrammarUtils.getPlural("food_category"));
    }

}
