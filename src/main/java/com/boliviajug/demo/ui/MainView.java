package com.boliviajug.demo.ui;

import com.boliviajug.demo.backend.modelo.Cliente;
import com.boliviajug.demo.backend.modelo.TipoTarjeta;
import com.boliviajug.demo.backend.repositorio.IClienteRepositorio;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;


@Route("")
@PWA(name = "BoliviaJug", shortName = "BoliviaJUG")
public class MainView extends Composite<VerticalLayout> {

    private Button refresh = new Button("", VaadinIcon.REFRESH.create());
    private Button add = new Button("", VaadinIcon.PLUS.create());
    private Button edit = new Button("", VaadinIcon.PENCIL.create());

    private final IClienteRepositorio repository;
    private Grid<Cliente> grid = new Grid<>(Cliente.class);

    @Autowired
    public MainView(IClienteRepositorio repository) {
        this.repository=repository;
        initLayout();
        initBehavior();
        refresh();

    }

    private void initLayout() {
        HorizontalLayout header = new HorizontalLayout(refresh, add, edit);
        grid.setColumns("id","nombre","direccion","ciudad","telefono","email");
        //grid.addComponentColumn(user -> new Button("Delete", e -> deleteClicked(user)));
        grid.setSizeFull();
        getContent().add(header, grid);
        getContent().expand(grid);
        getContent().setSizeFull();
        getContent().setMargin(false);
        getContent().setPadding(false);
    }

    private void updateHeader() {
        boolean selected = !grid.asSingleSelect().isEmpty();
        edit.setEnabled(selected);
    }

    private void initBehavior() {
        grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
        refresh.addClickListener(e -> refresh());
        add.addClickListener(e -> showAddDialog());
        edit.addClickListener(e -> showEditDialog());
    }


    public void refresh() {
        grid.setItems(repository.findAll());
        updateHeader();
    }


    private void showAddDialog() {
        UserFormDialog dialog = new UserFormDialog("Nuevo Cliente", new Cliente());
        dialog.open();
    }

    private void showEditDialog() {
        UserFormDialog dialog = new UserFormDialog("Actualizar Cliente", grid.asSingleSelect().getValue());
        dialog.open();
    }


    private class UserFormDialog extends Dialog {

        private TextField nombre = new TextField("Nombre Completo");
        private TextField direccion = new TextField("Direccion");
        private TextField ciudad = new TextField("Ciudad");
        private NumberField telefono = new NumberField("Telefono");
        private EmailField email = new EmailField("Email");
        private ComboBox<TipoTarjeta> tipoTarjeta = new ComboBox<>("Tipo Tarjeta");
        private Button cancel = new Button("Cancel");
        private Button save = new Button("Save", VaadinIcon.CHECK.create());

        public UserFormDialog(String caption, Cliente cliente) {
            initLayout(caption);
            initBehavior(cliente);
            tipoTarjeta.setItems(TipoTarjeta.values());
        }

        private void initLayout(String caption) {
            save.getElement().setAttribute("theme", "primary");
            HorizontalLayout buttons = new HorizontalLayout(cancel, save);
            buttons.setSpacing(true);
            nombre.setRequiredIndicatorVisible(true);
            FormLayout formLayout = new FormLayout(new H2(caption), nombre, direccion, ciudad,telefono, email, tipoTarjeta );
            VerticalLayout layout = new VerticalLayout(formLayout, buttons);
            layout.setAlignSelf(FlexComponent.Alignment.END, buttons);
            add(layout);
        }

        private void initBehavior(Cliente cliente) {
            BeanValidationBinder<Cliente> binder = new BeanValidationBinder<>(Cliente.class);
            binder.bindInstanceFields(this);
            binder.readBean(cliente);
            cancel.addClickListener(e -> close());
            save.addClickListener(e -> {
                try {
                    binder.validate();
                    binder.writeBean(cliente);
                    repository.save(cliente);
                    close();
                    refresh();
                    Notification.show("Customer saved");
                } catch (ValidationException ex) {
                    Notification.show("Please fix the errors and try again");
                }
            });
        }

    }


}