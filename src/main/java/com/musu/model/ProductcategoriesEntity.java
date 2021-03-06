package com.musu.model;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by User on 19.11.2016.
 */
@Entity
@Table(name = "productcategories", schema = "mydatabase", catalog = "")
public class ProductcategoriesEntity {
    private int categoryId;
    private String categoryName;
    private Set<ProductsEntity> products;
    @Id
    @Column(name = "CategoryID")
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "CategoryName")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    @OneToMany(mappedBy = "productcategoriesByProductCategoryId", cascade = CascadeType.ALL)
    public Set<ProductsEntity> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductsEntity> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductcategoriesEntity that = (ProductcategoriesEntity) o;

        if (categoryId != that.categoryId) return false;
        if (categoryName != null ? !categoryName.equals(that.categoryName) : that.categoryName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = categoryId;
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        return result;
    }
}
