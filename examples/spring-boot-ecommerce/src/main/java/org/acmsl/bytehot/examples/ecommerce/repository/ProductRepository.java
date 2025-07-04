/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ProductRepository.java
 *
 * Author: Claude Code
 *
 * Class name: ProductRepository
 *
 * Responsibilities:
 *   - Provide data access methods for Product entities
 *   - Support complex queries for product management
 *   - Enable efficient product search and filtering
 *
 * Collaborators:
 *   - Product: Entity being managed
 *   - ProductService: Uses this repository for data access
 */
package org.acmsl.bytehot.examples.ecommerce.repository;

import org.acmsl.bytehot.examples.ecommerce.model.Product;
import org.acmsl.bytehot.examples.ecommerce.model.ProductCategory;
import org.acmsl.bytehot.examples.ecommerce.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity data access.
 * @author Claude Code
 * @since 2025-07-04
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds a product by SKU.
     * @param sku The product SKU
     * @return The product if found
     */
    Optional<Product> findBySku(final String sku);

    /**
     * Checks if a product exists with the given SKU.
     * @param sku The product SKU
     * @return true if product exists, false otherwise
     */
    boolean existsBySku(final String sku);

    /**
     * Finds products by category and status.
     * @param category The product category
     * @param status The product status
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByCategoryAndStatus(final ProductCategory category,
                                        final ProductStatus status,
                                        final Pageable pageable);

    /**
     * Finds products by status.
     * @param status The product status
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByStatus(final ProductStatus status, final Pageable pageable);

    /**
     * Finds products with stock quantity less than the specified threshold.
     * @param threshold The stock threshold
     * @return List of products with low stock
     */
    List<Product> findByStockQuantityLessThan(final Integer threshold);

    /**
     * Finds products with stock quantity between the specified range.
     * @param minStock Minimum stock quantity
     * @param maxStock Maximum stock quantity
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByStockQuantityBetween(final Integer minStock,
                                           final Integer maxStock,
                                           final Pageable pageable);

    /**
     * Finds products by name containing the search term (case insensitive).
     * @param name The name search term
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByNameContainingIgnoreCase(final String name, final Pageable pageable);

    /**
     * Finds products by name or description containing the search terms.
     * @param name The name search term
     * @param description The description search term
     * @param pageable Pagination information
     * @return Page of matching products
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Product> findByNameContainingOrDescriptionContaining(@Param("name") final String name,
                                                             @Param("description") final String description,
                                                             final Pageable pageable);

    /**
     * Finds products by price range.
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByPriceBetween(final BigDecimal minPrice,
                                   final BigDecimal maxPrice,
                                   final Pageable pageable);

    /**
     * Finds products by category.
     * @param category The product category
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByCategory(final ProductCategory category, final Pageable pageable);

    /**
     * Finds active products by category with available stock.
     * @param category The product category
     * @param pageable Pagination information
     * @return Page of matching products
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.category = :category AND " +
           "p.status = 'ACTIVE' AND " +
           "p.stockQuantity > 0")
    Page<Product> findAvailableProductsByCategory(@Param("category") final ProductCategory category,
                                                final Pageable pageable);

    /**
     * Finds top-selling products by category.
     * This query would typically join with order data, but for simplicity
     * we'll order by stock quantity (assuming popular items have lower stock).
     * @param category The product category
     * @param pageable Pagination information
     * @return Page of top-selling products
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.category = :category AND " +
           "p.status = 'ACTIVE' " +
           "ORDER BY p.stockQuantity ASC")
    Page<Product> findTopSellingProductsByCategory(@Param("category") final ProductCategory category,
                                                  final Pageable pageable);

    /**
     * Finds products that need restocking.
     * @param threshold The minimum stock threshold
     * @param status The product status to consider
     * @return List of products needing restock
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.stockQuantity <= :threshold AND " +
           "p.status = :status")
    List<Product> findProductsNeedingRestock(@Param("threshold") final Integer threshold,
                                           @Param("status") final ProductStatus status);

    /**
     * Counts products by category.
     * @param category The product category
     * @return Number of products in the category
     */
    long countByCategory(final ProductCategory category);

    /**
     * Counts products by status.
     * @param status The product status
     * @return Number of products with the status
     */
    long countByStatus(final ProductStatus status);

    /**
     * Finds products created within the last N days.
     * @param days Number of days to look back
     * @param pageable Pagination information
     * @return Page of recently created products
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.createdAt >= CURRENT_TIMESTAMP - :days DAY " +
           "ORDER BY p.createdAt DESC")
    Page<Product> findRecentProducts(@Param("days") final Integer days,
                                   final Pageable pageable);

    /**
     * Finds products updated within the last N days.
     * @param days Number of days to look back
     * @param pageable Pagination information
     * @return Page of recently updated products
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.updatedAt >= CURRENT_TIMESTAMP - :days DAY " +
           "ORDER BY p.updatedAt DESC")
    Page<Product> findRecentlyUpdatedProducts(@Param("days") final Integer days,
                                            final Pageable pageable);
}