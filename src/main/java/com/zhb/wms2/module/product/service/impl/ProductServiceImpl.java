package com.zhb.wms2.module.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.service.ProductService;
/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:07
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService{

}
