## Registration Service

### Endpoints
- `POST /registrations` - создание регистрации (в ответ возвращается номер заявки и четырехзначный пароль доступа)
- `PATCH /registrations` - обновление заявки (в dto приходит номер и пароль, обновление происходит, если они введены корректно. Обновить можно только `username`, `email`, `phone`)
- `GET /registrations/{registrationId}` - получение регистрации по `id` (не возвращается номер заявки и пароль)
- `GET /registrations?page={page}&size={size}&eventId={eventId}` - получение списка регистраций с пагинацией и с обязательным указанием `id` события (не возвращается номер заявки и пароль)
- `DELETE /registrations` - удаление регистрации (по связке номера + пароля из dto)

### Models
Модель `Registration` включает следующие поля: 
- username
- email
- phone
- eventId
- номер заявки
- сгенерированный пароль