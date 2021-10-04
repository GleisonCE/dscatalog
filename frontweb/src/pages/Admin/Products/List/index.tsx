import { AxiosRequestConfig } from "axios";
import { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import ProductCrudCard from "pages/Admin/Products/ProductCrudCard";
import { Product } from "types/products";
import { SpringPage } from "types/vendor/spring";
import { requestBackend } from "util/requests";
import Pagination from "components/Pagination";

import "./styles.css";
import ProductFilter, { ProductFilterData } from "components/ProductFilter";

type ControlComponentsData = {
  activePage: number;
  filterData: ProductFilterData;
};

const List = () => {
  const [page, setPage] = useState<SpringPage<Product>>();
  const [controlComponentsData, setControlComponentsData] =
    useState<ControlComponentsData>({ activePage: 0, filterData: { name: "", category: null } });

  const getProucts = useCallback(() => {
    const config: AxiosRequestConfig = {
      method: "GET",
      url: "/products",
      params: {
        page: controlComponentsData.activePage,
        size: 12,
        name: controlComponentsData.filterData.name,
        categoryId: controlComponentsData.filterData.category?.id
      },
    };
    requestBackend(config).then((response) => {
      setPage(response.data);
    });
  }, [controlComponentsData]);

  const handlePageChange = (pageNumber: number) => {
    setControlComponentsData({
      activePage: pageNumber,
      filterData: controlComponentsData.filterData,
    });
  };

  const handleSubmitFilter = (data: ProductFilterData) => {
    setControlComponentsData({
      activePage: 0,
      filterData: data,
    });
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
        <ProductFilter onSubmitFilter={handleSubmitFilter} />
      </div>
      <div className="row">
        {page?.content.map((product) => (
          <div key={product.id} className="col-sm-6 col-md-12">
            <ProductCrudCard product={product} onDelete={getProucts} />
          </div>
        ))}
      </div>
      <Pagination
        forcePage={page?.number}
        pageCount={page ? page.totalPages : 0}
        range={3}
        onChange={handlePageChange}
      />
    </div>
  );
};

export default List;
