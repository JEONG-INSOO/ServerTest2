package com.kh.product.dao;

import com.kh.product.dao.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductDAOImpl implements ProductDAO {

  private final NamedParameterJdbcTemplate template;

  @Override
  public Long save(Product product) {

    StringBuffer sql = new StringBuffer();
    sql.append("insert into product(product_id,pname,quantity,price) ");
    sql.append("values(product_product_id_seq.nextval, :pname , :quantity, :price) ");

    // SQL 파라미터 자동매핑
    SqlParameterSource param = new BeanPropertySqlParameterSource(product); //
    KeyHolder keyHolder = new GeneratedKeyHolder();
    template.update(sql.toString(),param,keyHolder,new String[]{"product_id"});

    long productId = keyHolder.getKey().longValue();    //상품아이디
    return productId;
  }

  private RowMapper<Product> productRowMapper(){
    return (rs,rowNum)->{

      Product product = new Product();
      product.setProductId(rs.getLong("product_id"));
      product.setPname(rs.getString("pname"));
      product.setQuantity(rs.getLong("quantity"));
      product.setPrice(rs.getLong("price"));

      return product;
    };
  }

  @Override
  public Optional<Product> findById(Long productId) {

    StringBuffer sql = new StringBuffer();
    sql.append("select product_id,pname,quantity,price ");
    sql.append("  from product ");
    sql.append(" where product_id = :id ");

    MyRowMapper myRowMapper = new MyRowMapper();
    try {
      Map<String, Long> param = Map.of("id", productId);

      Product product = template.queryForObject(sql.toString(), param, myRowMapper);
      return Optional.of(product);
    }catch(EmptyResultDataAccessException e){
      return Optional.empty();
    }
  }

  @Override
  public List<Product> findAll() {

    StringBuffer sql = new StringBuffer();
    sql.append("  select product_id, pname, quantity, price ");
    sql.append("    from product ");
    sql.append("order by product_id desc");

    List<Product> list = template.query(sql.toString(), BeanPropertyRowMapper.newInstance(Product.class));
    return list;
  }

  @Override
  public int deleteById(Long productId) {
    String sql = "delete from product where product_id = :productId";

    int deletedRowCnt = template.update(sql, Map.of("productId", productId));

    return deletedRowCnt;
  }

  @Override
  public int updateById(Long productId, Product product) {

    StringBuffer sql = new StringBuffer();
    sql.append("update product ");
    sql.append("   set pname = :pname, quantity = :quantity, price = :price ");
    sql.append(" where product_id = :product_id ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("pname", product.getPname())
        .addValue("quantity", product.getQuantity())
        .addValue("price", product.getPrice())
        .addValue("product_id",productId);

    int updatedRows = template.update(sql.toString(), param);

    return updatedRows;
  }
}
