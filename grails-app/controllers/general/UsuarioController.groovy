package general

import org.springframework.dao.DataIntegrityViolationException

class UsuarioController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [usuarioList: Usuario.list(params), usuarioTotal: Usuario.count()]
    }

    def create() {
        [usuario: new Usuario(params)]
    }

    def save() {
        def usuario = new Usuario(params)
        if (!usuario.save(flush: true)) {
            render(view: "create", model: [usuario: usuario])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'usuario.label', default: 'Usuario'), usuario.id])
        redirect(action: "show", id: usuario.id)
    }

    def show() {
        def usuario = Usuario.get(params.id)
        if (!usuario) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "list")
            return
        }

        [usuario: usuario]
    }

    def edit() {
        def usuario = Usuario.get(params.id)
        if (!usuario) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "list")
            return
        }

        [usuario: usuario]
    }

    def update() {
        def usuario = Usuario.get(params.id)
        if (!usuario) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (usuario.version > version) {
                usuario.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'usuario.label', default: 'Usuario')] as Object[],
                          "Another user has updated this Usuario while you were editing")
                render(view: "edit", model: [usuario: usuario])
                return
            }
        }

        usuario.properties = params

        if (!usuario.save(flush: true)) {
            render(view: "edit", model: [usuario: usuario])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'usuario.label', default: 'Usuario'), usuario.id])
        redirect(action: "show", id: usuario.id)
    }

    def delete() {
        def usuario = Usuario.get(params.id)
        if (!usuario) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "list")
            return
        }

        try {
            usuario.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
