import { AxiosRequestConfig } from "axios";
import { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import ProductCrudCard from "pages/Admin/Products/ProductCrudCard";
import { Product } from "types/products";
import { SpringPage } from "types/vendor/spring";
import { requestBackend } from "util/requests";
import Pagination from "components/Pagination";

import "./styles.css";
import ProductFilter from "components/ProductFilter";

type ControlComponentsData = {
  activePage: number;
};

const List = () => {
  const [page, setPage] = useState<SpringPage<Product>>();
  const [controlComponentsData, setControlComponentsData] =
    useState<ControlComponentsData>({ activePage: 0 });

  const getProucts = useCallback(() => {
    const config: AxiosRequestConfig = {
      method: "GET",
      url: "/products",
      params: {
        page: controlComponentsData.activePage,
        size: 12,
      },
    };
    requestBackend(config).then((response) => {
      setPage(response.data);
    });
  }, [controlComponentsData]);

  const handlePageChange = (pageNumber: number) => {
    setControlComponentsData({ activePage: pageNumber });
  };

  useEffect(() => {
    getProucts();
  }, [getProucts]);

  return (
    <div className="product-crud-container">
      <div className="product-crud-bar-container">
        <Link to="/admin/products/create">
          <button className="btn btn-primary text-white btn-crud-add">
            ADICIONAR
          </button>
        </Link>
        <ProductFilter />        
      </div>
      <div className="row">
        {page?.content.map((product) => (
          <div key={product.id} className="col-sm-6 col-md-12">
            <ProductCrudCard product={product} onDelete={getProucts} />
          </div>
        ))}
      </div>
      <Pagination
        pageCount={page ? page.totalPages : 0}
        range={3}
        onChange={handlePageChange}
      />
    </div>
  );
};

export default List;
