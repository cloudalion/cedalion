data Sum a b = Plus a b
data Prod a b = Times a b

class Expr e where
  eval                 :: e -> Integer

instance Expr Integer where
  eval x = x

instance (Expr t1, Expr t2) => Expr (Sum t1 t2) where
  eval (Plus a b) = (eval a) + (eval b)

instance (Expr t1, Expr t2) => Expr (Prod t1 t2) where
  eval (Times a b) = (eval a) * (eval b)

instance Expr Bool where
  eval True = 1
  eval False = 0

twice a = Plus a a
square a = eval (Times a a)

instance (Expr x) => Eq x where
  a == b	= (eval a) == (eval b)

