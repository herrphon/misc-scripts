String result = "";

this.binding.variables.each {key, value -> 
    result += "${key} = ${value} \n"
}

return result
