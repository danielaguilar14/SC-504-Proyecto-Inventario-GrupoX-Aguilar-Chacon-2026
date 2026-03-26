package cr.ac.tiquiciatech;

import cr.ac.tiquiciatech.dao.CategoriaDAO;
import cr.ac.tiquiciatech.model.Categoria;
import java.util.List;

public class TestCategoria {

    public static void main(String[] args) {
        CategoriaDAO dao = new CategoriaDAO();

        Categoria nueva = new Categoria();
        nueva.setNombre("Mouse");
        nueva.setDescripcion("Dispositivo apuntador");
        nueva.setEstado("ACTIVO");
        dao.insertarCategoria(nueva);

        System.out.println("==== LISTADO DE CATEGORIAS ====");
        List<Categoria> categorias = dao.listarCategorias();
        for (Categoria c : categorias) {
            System.out.println(c);
        }
    }
}