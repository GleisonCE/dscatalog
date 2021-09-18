import { AxiosRequestConfig } from "axios";
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import ProductCrudCard from "pages/Admin/Products/ProductCrudCard";
import { Product } from "types/products";
import { SpringPage } from "types/vendor/spring";
import { requestBackend } from "util/requests";

import "./styles.css";

const List = () => {
  const [page, setPage] = useState<SpringPage<Product>>();

  useEffect(() => {
    const config: AxiosRequestConfig = {
      method: "GET",
      url: "/products",
      params: {
        page: 0,
        size: 12,
      },
    };
    requestBackend(config).then((resp) => {
      setPage(resp.data);
    });
  }, []);

  return (
    <div className="product-crud-container">
      <div className="product-crud-bar-container">
        <Link to="/admin/products/creat">
          <button className="btn btn-primary text-white btn-crud-add">
            ADICIONAR
          </button>
        </Link>

        <div className="base-card product-filter_container">SearchBar</div>
      </div>
      <div className="row">
        {page?.content.map((product) => (
          <div key={product.id} className="col-sm-6 col-md-12">
            <ProductCrudCard product={product} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default List;
