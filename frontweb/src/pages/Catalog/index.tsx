import { AxiosRequestConfig } from "axios";
import Pagination from "components/Pagination";
import ProductCard from "components/ProductCard";
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Product } from "types/products";
import { SpringPage } from "types/vendor/spring";
import { requestBackend } from "util/requests";
import CardLoader from "./CardLoader";
import "./styles.css";

const Catalog = () => {
  const [page, setPage] = useState<SpringPage<Product>>();
  const [isLoading, setIsLoading] = useState(false);

  const getProucts = (pageNumber: number) => {
    const params: AxiosRequestConfig = {
      method: "GET",
      url: "/products",
      params: {
        page: pageNumber,
        size: 12,
      },
    };
    setIsLoading(true);
    requestBackend(params)
      .then((resp) => {
        setPage(resp.data);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  useEffect(() => {
    getProucts(0);
  }, []);

  return (
    <div className="container my-4 catalog-container">
      <div className="row catalog-title-container">
        <h1>Catálogo de Produtos</h1>
      </div>
      <div className="row">
        {isLoading ? (
          <CardLoader />
        ) : (
          page?.content.map((product) => (
            <div className="col-sm-6 col-lg-4 col-xl-3" key={product.id}>
              <Link to="/products/1">
                <ProductCard product={product} />
              </Link>
            </div>
          ))
        )}
      </div>
      <Pagination pageCount={page ? page.totalPages : 0} range={3} onChange={getProucts} />
    </div>
  );
};

export default Catalog;
