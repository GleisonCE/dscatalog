import { Route, Switch } from "react-router-dom";
import Navbar from "./Navbar";
import "./styles.css";

const Admin = () => {
  return (
    <div className="admin-container">
      <Navbar />
      <div className="admin-content">
        <Switch>
          <Route path="/admin/products">
            <h1>CRUD de Produtos</h1>
          </Route>
          <Route path="/admin/categories">
            <h1>CRUD de Categoria</h1>
          </Route>
          <Route path="/admin/users">
            <h1>CRUD de Usuarios</h1>
          </Route>
        </Switch>
      </div>
    </div>
  );
};

export default Admin;
